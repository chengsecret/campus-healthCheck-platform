package xyz.ctstudy.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import xyz.ctstudy.commons.RedisKey;
import xyz.ctstudy.commons.Result;
import xyz.ctstudy.dao.RecordMapper;
import xyz.ctstudy.entity.Record;
import xyz.ctstudy.service.QiniuService;
import xyz.ctstudy.service.RecordService;
import xyz.ctstudy.utils.DateUtil;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

@Service
@Slf4j
public class RecordServiceImpl implements RecordService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RecordMapper recordMapper;
    @Autowired
    QiniuService qiniuService;

    @Override
    public Result<Boolean> hasCheck(String openid) {
        //每个用户都有自己的打卡记录（只保存一条）
        String redisKey = RedisKey.USER_RECORD_APPLY + openid;
        Record record = (Record) redisTemplate.opsForValue().get(redisKey);
        if(record != null){
            return Result.SUCCESS(record.getIscheck() != 0); //0表示未审核
        } else { //redis中没有数据
            record = recordMapper.getRecordByOenid(openid);
            if(record == null){ //数据库中没有数据
                log.info(openid+"用户在数据库中创建record");
                record = new Record(0,openid,1,1, DateUtil.getNowDay(),"","",2);
                if(!recordMapper.addRecord(record)){ //数据库中添加数据
                    return Result.FAIL();
                }
            }
            redisTemplate.opsForValue().set(redisKey,record, Duration.ofDays(7));
            return Result.SUCCESS(record.getIscheck() != 0);
        }
    }

    @Override
    public Result apply(Record record, String tripPictureBase64, String tripType, String reportPictureBase64, String reportTpye) {
        //将base64的图片解析为字节码

        try {
            byte[] tripByte = Base64.decodeBase64(tripPictureBase64);
            File fileTrip;
            //将图片写入临时文件
            fileTrip = File.createTempFile(String.valueOf(System.currentTimeMillis()), tripType);
            FileUtils.writeByteArrayToFile(fileTrip, tripByte);

            //上传七牛云
            String tripURL = qiniuService.uploadFile(fileTrip); //返回图片的url地址
            record.setTripurl(tripURL);

            //核酸检测报告可能为空
            if(ObjectUtils.isEmpty(reportPictureBase64)){
                record.setReporturl("");
            }else {
                byte[] reportByte = Base64.decodeBase64(reportPictureBase64);
                File reportFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), tripType);
                FileUtils.writeByteArrayToFile(reportFile, reportByte);
                String reportURL = qiniuService.uploadFile(reportFile);
                record.setReporturl(reportURL);
            }
            //更新redis与数据库
            if(!recordMapper.updateRecord(record)){
                return Result.FAIL();
            }
            String redisKey = RedisKey.USER_RECORD_APPLY + record.getOpenid();
            redisTemplate.opsForValue().set(redisKey,record,Duration.ofDays(7));

            return Result.SUCCESS();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.FAIL();
        }
    }
}
