package xyz.ctstudy.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.ctstudy.commons.Result;
import xyz.ctstudy.entity.Record;
import xyz.ctstudy.service.RecordService;
import xyz.ctstudy.utils.DateUtil;

@RestController
public class RecordController {

    @Autowired
    private RecordService recordService;

    /**
     * 查看打卡记录是否被审核
     * @param openid
     * @return
     */
    @GetMapping("hasCheck")
    Result<Boolean> hasCheck(@RequestHeader("openid") String openid){
        return recordService.hasCheck(openid);
    }

    /**
     * 申请绿码
     * @param openid
     * @param jsonObject
     * @return
     */
    @PostMapping("apply")
    Result apply(@RequestHeader("openid") String openid,@RequestBody JSONObject jsonObject){
        String tripPictureBase64 = jsonObject.getString("tripPicture");
        String tripType = jsonObject.getString("tripType");
        String reportPictureBase64 = jsonObject.getString("reportPicture");
        String reportType = jsonObject.getString("reportType");

        Record record = new Record();
        record.setOpenid(openid);
        record.setColor(jsonObject.getIntValue("color"));
        record.setReason(jsonObject.getIntValue("reason"));
        record.setTime(DateUtil.getNowDay());
        record.setIscheck(0);

        return recordService.apply(record,tripPictureBase64,tripType,reportPictureBase64,reportType);
    }
}
