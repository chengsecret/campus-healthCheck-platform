package xyz.ctstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import xyz.ctstudy.commons.Result;
import xyz.ctstudy.entity.RecordDaka;
import xyz.ctstudy.service.DakaService;
import xyz.ctstudy.utils.DateUtil;

import java.util.LinkedList;

@RestController
public class DakaController {

    @Autowired
    private DakaService dakaService;

    /**
     * 判断今日是否已经签到
     * @param openid
     * @return
     */
    @GetMapping("hasCheck")
    public Result hasCkeck(@RequestHeader("openid") String openid){
        if(dakaService.hasCheck(openid)){
            return Result.SUCCESS(); //今日已打卡
        }else {
            return Result.FAIL(); //今日未打卡
        }
    }

    /**
     * 当天的健康打卡
     * @param record
     * @param openid
     * @return
     */
    @PostMapping("check")
    public Result check(RecordDaka record , @RequestHeader("openid") String openid){
        record.setOpenid(openid);
        record.setTime(DateUtil.getNowDay());
        return dakaService.check(record);
    }

    /**
     * 获得用户打卡记录的日期
     * @param openid
     * @return
     */
    @GetMapping("getRecord")
    public LinkedList<String> getRecord(@RequestHeader("openid") String openid){
        return dakaService.getRecords(openid);
    }

    @GetMapping("getContinuousNum")
    public int getContinuousNum(@RequestHeader("openid") String openid){
        return dakaService.getContinuousNum(openid);
    }
}
