package xyz.ctstudy.service.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xyz.ctstudy.commons.RedisKey;
import xyz.ctstudy.dao.UserMapper;
import xyz.ctstudy.entity.HealthInfo;
import xyz.ctstudy.entity.UserHealthInfo;
import xyz.ctstudy.utils.DateUtil;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;

@Service
@Slf4j
public class HealthInfoScheduleService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 每天23点30分，将没有打卡且通行码为绿色的，将通行码改为紫色
     * 用户的打卡时间为5点~22点
     */
    @Scheduled(cron = "0 30 23 * * ?")
    public void updateRiskArea(){
        log.info(DateUtil.getNowTime() + "---未打卡绿码用户变紫码");
        String dayBeginDate = DateUtil.getDayBeginDate(new Date().getTime()); //获得前一天的时间
        //获取今日未打卡且为绿码的用户健康信息
        ArrayList<String> openids = userMapper.getHealthInfo(1, dayBeginDate);
        //将数据库中没有打卡且通行码为绿色的，将通行码改为紫色
        userMapper.updateHealthInfoColorAndReason(1,dayBeginDate,2,2);
        //将redis中今日未打卡的用户的信息更新
        for (String openid : openids){
            String redisKey = RedisKey.WX_USER_ID + openid;
            UserHealthInfo userHealthInfo = (UserHealthInfo) redisTemplate.opsForValue().get(redisKey);
            if(userHealthInfo != null){
                userHealthInfo.setColor(2);
                userHealthInfo.setReason(2);
                //将信息存入redis
                redisTemplate.opsForValue().set(redisKey,userHealthInfo, Duration.ofDays(7));
            }
        }
    }
}
