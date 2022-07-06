package xyz.ctstudy.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import xyz.ctstudy.commons.RedisKey;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class RiskAreaUtil {
    @Autowired
    private  RestTemplate restTemplate;
    @Autowired
    private  RedisTemplate redisTemplate;

    //中高风险地区信息的url
    private static final String riskURL = "https://m.sm.cn/api/rest?format=json&method=Huoshenshan.riskArea&_=1628665447912";
    //百度地图的ak
    private static final String ak = "thCuhGo0p72ktzHtrRc26ulA1yqTx0kk";

    /**
     * 获取中高风险地区，通过百度地图api逆向解析成经纬度，然后将数据放入redis中
     * @return
     */
    public  boolean getRiskArea(){
        log.info(DateUtil.getNowTime() + "获取中高风险地区信息");
        //首先删除原有的中高风险地区信息
        if(Boolean.TRUE.equals(redisTemplate.hasKey(RedisKey.WX_RiskArea))){
            redisTemplate.delete(RedisKey.WX_RiskArea);
        }

        //获取当天所有的中高风险地区
        JSONObject jsonObject = Objects.requireNonNull(restTemplate.getForObject(riskURL, JSONObject.class))
                .getJSONObject("data").getJSONObject("map");
        //获取中风险地区信息
        JSONObject lowRisk = jsonObject.getJSONObject("1");
        //获取高风险地区信息
        JSONObject highRisk = jsonObject.getJSONObject("2");
        if(!writeInRedis(lowRisk)){
            return false;
        }
        if(!writeInRedis(highRisk)){
            return false;
        }
        return true;
    }

    private  boolean writeInRedis(JSONObject jsonObject){
        Set<String> set = jsonObject.keySet();
        for(String province : set){
            JSONArray jsonArray = jsonObject.getJSONArray(province);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String address = object.getString("province") +
                        object.getString("city") +
                        object.getString("addr");
                //通过百度地图api，根据地址逆向解析出经纬度
                Map<String,Double> lngAndLat = getLngAndLat(address);
//                System.out.println(lngAndLat.get("lng") + "====="+ lngAndLat.get("lat"));
                Point point = new Point(lngAndLat.get("lng"), lngAndLat.get("lat"));
                //将经纬度放到redis中
                redisTemplate.opsForGeo().add(RedisKey.WX_RiskArea, point, address);
            }
        }
        return true;
    }

    //百度地图的api，查询地址的经纬度
    private  Map<String,Double> getLngAndLat(String address){

        String url = "https://api.map.baidu.com/geocoding/v3/?address=" +
                address + "&output=json&ak=" +
                ak;
        Map<String,Double> map=new HashMap<String, Double>();

        StringBuilder json = new StringBuilder();
        try {
            URL oracle = new URL(url);
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream(),"UTF-8"));
            String inputLine = null;
            while ( (inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
        }

        JSONObject obj = JSON.parseObject(json.toString());
        if(obj.get("status").toString().equals("0")){
            double lng=obj.getJSONObject("result").getJSONObject("location").getDouble("lng");
            double lat=obj.getJSONObject("result").getJSONObject("location").getDouble("lat");
            map.put("lng", lng);
            map.put("lat", lat);
        }
        return map;
    }


}
