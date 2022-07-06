package xyz.ctstudy.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import xyz.ctstudy.commons.RedisKey;
import xyz.ctstudy.commons.Result;
import xyz.ctstudy.dao.UserMapper;
import xyz.ctstudy.entity.HealthInfo;
import xyz.ctstudy.entity.User;
import xyz.ctstudy.entity.UserHealthInfo;
import xyz.ctstudy.service.UserService;
import xyz.ctstudy.utils.DateUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Service
@Slf4j
public class UserSeviceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${wx.applet.appId}")
    private String appId;
    @Value("${wx.applet.appSecret}")
    private String appSecret;

    @Override
    public Result<HashMap<String, Object>> userLogin(String code) {

        String WX_API = "https://api.weixin.qq.com/sns/jscode2session";
        String url = WX_API +
                "?appid=" + appId +
                "&secret=" + appSecret +
                "&js_code=" + code +
                "&grant_type=authorization_code";
        //获取用户的openid
        String data = restTemplate.getForObject(url, String.class);

        if(StringUtils.contains(data,"errcode") || StringUtils.isEmpty(data)){
            log.info("code错误");
            return Result.FAIL();
        }

        //查询redis中是否存在该用户的信息
        JSONObject jsonObject = JSON.parseObject(data);
        String openid = jsonObject.getString("openid");
        String redisKey = RedisKey.WX_USER_ID + openid;
        UserHealthInfo userHealthInfo = (UserHealthInfo) redisTemplate.opsForValue().get(redisKey);

        if(userHealthInfo == null){ //redis中没有该key的情况
            User user = userMapper.findUser(openid);

            if(user == null){ //如果数据库中没有该用户，则添加该用户
                user = new User(0,openid,"","","",-1);
                String dayBeginDate = DateUtil.getDayBeginDate(new Date().getTime()); //获得前一天的时间
                HealthInfo healthinfo = new HealthInfo(0, openid, 1, 1, dayBeginDate, 0);
                if(!addUserHealthInfo(user,healthinfo)){
                    log.info("新增用户失败");
                    return Result.FAIL();
                }
                userHealthInfo = new UserHealthInfo(user,healthinfo);
            }else{ //数据库中有该用户，则取出用户信息
                HealthInfo healthInfo = userMapper.findHealthInfo(openid);
                userHealthInfo = new UserHealthInfo(user,healthInfo);
            }
            //将信息存入redis
            redisTemplate.opsForValue().set(redisKey,userHealthInfo, Duration.ofDays(7));
        }

        //封装返回数据
        HashMap<String, Object> map = new HashMap<>();
        map.put("name",userHealthInfo.getName());
        map.put("num",userHealthInfo.getNum());
        map.put("classnum",userHealthInfo.getClassnum());
        map.put("token",openid); //由网关去封装

        return Result.SUCCESS(map);
    }

    @Override
    public Result updateUser(User user) {
        //判断学号是否合法（数据库中学号是唯一的）
        User userByNum = userMapper.findUserByNum(user.getNum());
        if(userByNum != null && !userByNum.getOpenid().equals(user.getOpenid())){
            return Result.FAIL("该学号已注册");
        }

        //根据班级号获得该用户的专业号mid
        Integer mid = userMapper.getMid(user.getClassnum());
        if ( mid == null) {
            return Result.FAIL("班级号不存在");
        }
        user.setMid(mid);

        //跟新数据库与redis中的信息
        if(!userMapper.updateUser(user)){
            return Result.FAIL("保存失败");
        }
        String redisKey = RedisKey.WX_USER_ID + user.getOpenid();
        UserHealthInfo userHealthInfo = (UserHealthInfo) redisTemplate.opsForValue().get(redisKey);
        if(userHealthInfo != null){ //redis中存在这条数据就跟新
            userHealthInfo.setName(user.getName());
            userHealthInfo.setNum(user.getNum());
            userHealthInfo.setClassnum(user.getClassnum());
            //将信息存入redis
            redisTemplate.opsForValue().set(redisKey,userHealthInfo, Duration.ofDays(7));
        }

        return Result.SUCCESS();
    }

    @Override
    public ArrayList<String> getClassnums() {
        return userMapper.getClassnums();
    }

    @Override
    public HealthInfo getHealthInfo(String openid) {
        HealthInfo healthInfo = null;
        String redisKey = RedisKey.WX_USER_ID + openid;
        UserHealthInfo userHealthInfo = (UserHealthInfo) redisTemplate.opsForValue().get(redisKey);
        if(userHealthInfo !=null){
            healthInfo = new HealthInfo();
            healthInfo.setOpenid(openid);
            healthInfo.setColor(userHealthInfo.getColor());
            healthInfo.setReason(userHealthInfo.getReason());
            healthInfo.setUpdatetime(userHealthInfo.getUpdatetime());
            healthInfo.setContinuousnum(userHealthInfo.getContinuousnum());
        } else {
            healthInfo = userMapper.findHealthInfo(openid);
        }
        return healthInfo;
    }

    @Override
    public UserHealthInfo getUserHealthInfo(String openid) {
        String redisKey = RedisKey.WX_USER_ID + openid;
        UserHealthInfo userHealthInfo = (UserHealthInfo) redisTemplate.opsForValue().get(redisKey);
        if(userHealthInfo == null){
            User user = userMapper.findUser(openid);
            HealthInfo healthInfo = userMapper.findHealthInfo(openid);
            userHealthInfo = new UserHealthInfo(user,healthInfo);
        }
        return userHealthInfo;
    }

    @Transactional
    boolean addUserHealthInfo(User user, HealthInfo healthinfo){
        if(userMapper.addUser(user) && userMapper.addHealthInfo(healthinfo)){
            return true;
        }
        return false;
    }
}
