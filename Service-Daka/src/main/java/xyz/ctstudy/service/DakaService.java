package xyz.ctstudy.service;

import xyz.ctstudy.commons.Result;
import xyz.ctstudy.entity.RecordDaka;

import java.util.LinkedList;

public interface DakaService {
    /**
     * 判断当日用户是否已经打卡
     * @param openid
     * @return
     */
    public boolean hasCheck(String openid);

    /**
     * 当天的健康打卡
     * @param record
     * @return
     */
    public Result check(RecordDaka record);

    /**
     * 获取用户打卡记录的日期
     * @param openid
     * @return
     */
    public LinkedList<String> getRecords(String openid);

    /**
     * 获取连续打卡天数
     * @param openid
     * @return
     */
    public int getContinuousNum(String openid);
}
