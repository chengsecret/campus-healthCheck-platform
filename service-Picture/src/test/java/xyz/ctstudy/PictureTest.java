package xyz.ctstudy;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import xyz.ctstudy.dao.RecordMapper;
import xyz.ctstudy.entity.Record;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.sql.Date;
import java.util.Hashtable;
import java.util.UUID;

@SpringBootTest
public class PictureTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void test() throws Exception {
        int rgb = new Color(255,0,0).getRGB();
//        new Color(0xFFFFFFFF)
        System.out.println(Integer.toHexString(rgb));
    }


}
