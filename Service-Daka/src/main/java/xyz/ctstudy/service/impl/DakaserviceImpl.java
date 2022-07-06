package xyz.ctstudy.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import xyz.ctstudy.commons.RedisKey;
import xyz.ctstudy.commons.Result;
import xyz.ctstudy.dao.RecordDakaMapper;
import xyz.ctstudy.dao.UserMapper;
import xyz.ctstudy.entity.HealthInfo;
import xyz.ctstudy.entity.RecordDaka;
import xyz.ctstudy.entity.User;
import xyz.ctstudy.entity.UserHealthInfo;
import xyz.ctstudy.service.DakaService;
import xyz.ctstudy.utils.DateUtil;
import xyz.ctstudy.utils.RiskAreaUtil;

import java.time.Duration;
import java.util.Date;
import java.util.LinkedList;

@Service
@Slf4j
public class DakaserviceImpl implements DakaService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RiskAreaUtil riskAreaUtil;
    @Autowired
    private RecordDakaMapper recordDakaMapper;

    @Override
    public boolean hasCheck(String openid) {
//        log.info("hascheck");
        //先查redis，有信息就直接返回
        //如果redis中没有信息，就查询数据库（不更新redis）
        String redisKey = RedisKey.WX_USER_ID + openid;
        UserHealthInfo userHealthInfo = (UserHealthInfo) redisTemplate.opsForValue().get(redisKey);
        if(userHealthInfo != null){
            String lastCheck = userHealthInfo.getUpdatetime(); //获得上一次打卡时间
            if(DateUtil.getNowDay().equals(lastCheck)){ //上一次打卡时间为今日
                return true;
            }else
                return false;
        } else { //redis中没有数据
            HealthInfo healthInfo = userMapper.findHealthInfo(openid);
            String lastCheck = healthInfo.getUpdatetime();
            if(DateUtil.getNowDay().equals(lastCheck)){ //上一次打卡时间为今日
                return true;
            }else
                return false;
        }
    }

    @Override
    public Result check(RecordDaka record) {
        //1.根据经纬度判断是否处于中高风险地区，以1.5km为比较范围
        //若redis中无中高风险地区，则获取它
        if(! Boolean.TRUE.equals(redisTemplate.hasKey(RedisKey.WX_RiskArea))){
            if(!riskAreaUtil.getRiskArea()){
                return Result.FAIL("无法获取中高风险地区");
            }
        }
        //获取用户提交的经纬度为中心，1.5km为半径的范围内中高风险地区的数量size
        Point point = new Point(record.getLongitude(), record.getLatitude());
        Circle circle = new Circle(point, new Distance(1.5, Metrics.KILOMETERS));
        GeoResults radius = redisTemplate.opsForGeo().radius(RedisKey.WX_RiskArea, circle);
        int size = radius.getContent().size();
        if(size > 0){
            record.setIsrisk(1); //用户处于中高风险地区
        }else{
            record.setIsrisk(0); //未处于中高风险地区
        }

        //2.将打卡记录先存放于redis的list数据类型中
        redisTemplate.opsForList().leftPush(RedisKey.WX_USER_RECORDDAKA,record);

        //3.更新redis中该用户healthinfo中的信息
        String redisKey = RedisKey.WX_USER_ID + record.getOpenid();
        UserHealthInfo userHealthInfo = (UserHealthInfo) redisTemplate.opsForValue().get(redisKey);
        if(userHealthInfo == null){//redis中没有数据就从数据库中取
            User user = userMapper.findUser(record.getOpenid());
            HealthInfo healthInfo = userMapper.findHealthInfo(record.getOpenid());
            userHealthInfo = new UserHealthInfo(user,healthInfo);
        }
        //跟新continuousnum（截止上次打卡时连续打卡天数）
        String dayBeginDate = DateUtil.getDayBeginDate(new Date().getTime()); //获得前一天的时间
        if(userHealthInfo.getUpdatetime().equals(dayBeginDate)){
            int continuousnum = userHealthInfo.getContinuousnum();
            userHealthInfo.setContinuousnum(continuousnum + 1);
        }else {
            userHealthInfo.setContinuousnum(1);
        }
        //更新updatetime
        userHealthInfo.setUpdatetime(DateUtil.getNowDay());
        //更新健康码颜色和原因
        updateColorAndReason(userHealthInfo,record);
        redisTemplate.opsForValue().set(redisKey,userHealthInfo, Duration.ofDays(7));

        return Result.SUCCESS();
    }

    @Override
    public LinkedList<String> getRecords(String openid) {
        //获取所有打卡记录的日期
        LinkedList<String> recordsDates = recordDakaMapper.getRecords(openid);
        //redis中的记录可能还未同步到数据库，所以要判断
        if(!recordsDates.contains(DateUtil.getNowDay())){ //不包含今日
            if(hasCheck(openid)){ //今日已打卡并且recordsDates中没有今日的日期，则加入今日日期
                recordsDates.add(DateUtil.getNowDate());
            }
        }

        return recordsDates;
    }

    @Override
    public int getContinuousNum(String openid) {
        String redisKey = RedisKey.WX_USER_ID + openid;
        UserHealthInfo userHealthInfo = (UserHealthInfo) redisTemplate.opsForValue().get(redisKey);
        String updatetime = null;
        //判断redis中是否有数据
        if(userHealthInfo != null){
            updatetime = userHealthInfo.getUpdatetime();
        } else { //redis中没有数据
            HealthInfo healthInfo = userMapper.findHealthInfo(openid);
            updatetime = healthInfo.getUpdatetime();
        }
        //如果是今天或者昨天打的卡，返回连续打卡天数；否则返回0
        if(DateUtil.getNowDay().equals(updatetime) ||
                DateUtil.getDayBeginDate(new Date().getTime()).equals(updatetime)){
            return userHealthInfo.getContinuousnum();
        }else {
            return 0;
        }
    }

    //当前建康码颜色（绿紫黄红1234）
    void updateColorAndReason(UserHealthInfo userHealthInfo, RecordDaka record){
        if(record.getZjpicture() != 3){ //浙江健康码非绿
            userHealthInfo.setColor(4); //红
            userHealthInfo.setReason(6);
        }else if(record.getState() != 0){ //健康状况为疑似/确诊
            userHealthInfo.setColor(4);  //红
            userHealthInfo.setReason(5);
        }else if(record.getIsrisk() == 1){ //用户处在中高风险地区
            if(userHealthInfo.getColor() != 4){ //如果是红码则不变
                userHealthInfo.setColor(3); //黄
                userHealthInfo.setReason(3);
            }
        }else{
            //为紫码并且连续打卡了14天，自动变绿码
            if(userHealthInfo.getColor() == 2 && userHealthInfo.getContinuousnum() == 14 ){
                userHealthInfo.setColor(1); //绿
            }
        }
    }
}
