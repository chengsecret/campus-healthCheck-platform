package xyz.ctstudy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.geo.*;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.web.client.RestTemplate;
import xyz.ctstudy.commons.RedisKey;
import xyz.ctstudy.dao.RecordDakaMapper;
import xyz.ctstudy.dao.UserMapper;
import xyz.ctstudy.entity.HealthInfo;
import xyz.ctstudy.entity.RecordDaka;
import xyz.ctstudy.entity.UserHealthInfo;
import xyz.ctstudy.utils.DateUtil;
import xyz.ctstudy.utils.RiskAreaUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

@SpringBootTest
public class HealthCheckTest {

    @Autowired
    private  RestTemplate restTemplate;
    @Autowired
    private  RedisTemplate redisTemplate;
    @Autowired
    private RiskAreaUtil riskAreaUtil;
    @Autowired
    private RecordDakaMapper recordDakaMapper;
    @Autowired
    private UserMapper userMapper;

    @Test
     void test() {

//        boolean b = userMapper.updateHealthInfoColorAndReason(1, "232",2,2);
//        System.out.println(b);

    }

}
