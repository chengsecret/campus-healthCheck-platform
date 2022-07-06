package xyz.ctstudy.controller;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import xyz.ctstudy.commons.RedisKey;
import xyz.ctstudy.entity.PassPictRedis;
import xyz.ctstudy.entity.UserHealthInfo;
import xyz.ctstudy.service.DakaService;
import xyz.ctstudy.service.QiniuService;
import xyz.ctstudy.utils.DateUtil;
import xyz.ctstudy.utils.PassPictUtil;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;

@RestController
public class PassPictureController {

    @Autowired
    private DakaService dakaService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    QiniuService qiniuService;

    /**
     * 获取通行码
     * @param openid
     */
    @GetMapping("getPassPict")
    String getPassPict(@RequestHeader("openid") String openid){
        String redisKey = RedisKey.USER_PASSPICT_INFO + openid;
        PassPictRedis passPictRedis = (PassPictRedis) redisTemplate.opsForValue().get(redisKey);
        // redis中有数据且是当天的数据
        if(passPictRedis != null && DateUtil.getNowDay().equals(passPictRedis.getDate())){
            return passPictRedis.getPictURL();
        }else {
            //生成二维码，dakaService使用了feign，调用了打卡服务
            UserHealthInfo userHealthInfo = dakaService.userHealthInfo(openid);
            BufferedImage bufferedImage = PassPictUtil.createImage(300,
                    userHealthInfo.toString(), userHealthInfo.getColor(), true);
            try {
                File file = File.createTempFile(String.valueOf(System.currentTimeMillis()), "jpg");
                ImageIO.write(bufferedImage,"jpg",file);
                //上传七牛云
                String passURL = qiniuService.uploadFile(file); //返回图片的url地址
                PassPictRedis passPictRedis1 = new PassPictRedis(DateUtil.getNowDate(), passURL);
                //放入redis
                redisTemplate.opsForValue().set(redisKey,passPictRedis1, Duration.ofDays(1));
                return passURL;
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

}
