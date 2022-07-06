package xyz.ctstudy.service.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xyz.ctstudy.commons.RedisKey;
import xyz.ctstudy.dao.RecordDakaMapper;
import xyz.ctstudy.dao.UserMapper;
import xyz.ctstudy.entity.HealthInfo;
import xyz.ctstudy.entity.RecordDaka;
import xyz.ctstudy.entity.UserHealthInfo;
import xyz.ctstudy.utils.DateUtil;

import java.util.List;

@Service
@Slf4j
public class RecordDakaScheduleService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RecordDakaMapper recordDakaMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 每天9点12点15点23点，将redis中的打卡记录同步到mysql中
     * // 秒 分 时 日 月 周几
     */
    @Scheduled(cron = "0 0 9,12,15,23 * * ?")
    public void writeRedisToSql(){
        log.info( DateUtil.getNowTime() + "---同步redis与数据库的信息");
        //redis事务，获取redis中的所有打卡记录，再删掉所有redis中的记录，表示已经同步到数据库
        SessionCallback<List<RecordDaka>> callback = new SessionCallback<List<RecordDaka>>() {
            @Override
            public List<RecordDaka> execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                //获取当前redis中尚未同步的打卡记录
                redisTemplate.opsForList().range(RedisKey.WX_USER_RECORDDAKA, 0, -1);
                //获取到了就删除
                redisTemplate.delete(RedisKey.WX_USER_RECORDDAKA);
                return (List<RecordDaka>) operations.exec().get(0);
            }
        };
        //打卡记录
        List<RecordDaka> list = (List<RecordDaka>) redisTemplate.execute(callback);

        for(RecordDaka record : list){
            //将打卡记录同步到数据库
            recordDakaMapper.addRecordDaka(record);
            //再将该记录对应的redis中的用户信息（健康码颜色、上一次打卡时间等）同步到数据库
            String redisKey = RedisKey.WX_USER_ID + record.getOpenid();
            UserHealthInfo userHealthInfo = (UserHealthInfo) redisTemplate.opsForValue().get(redisKey);
            if(userHealthInfo != null){
                HealthInfo healthInfo = new HealthInfo();
                healthInfo.setOpenid(record.getOpenid());
                healthInfo.setColor(userHealthInfo.getColor());
                healthInfo.setReason(userHealthInfo.getReason());
                healthInfo.setUpdatetime(userHealthInfo.getUpdatetime());
                healthInfo.setContinuousnum(userHealthInfo.getContinuousnum());
                userMapper.updateHealthInfo(healthInfo);
            }
        }
    }
}
