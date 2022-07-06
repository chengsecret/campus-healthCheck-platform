package xyz.ctstudy.dao;

import org.springframework.stereotype.Repository;
import xyz.ctstudy.entity.RecordDaka;

import java.util.LinkedList;

@Repository
public interface RecordDakaMapper {

    /**
     * 添加打卡记录
     * @param recordDaka
     * @return
     */
    public boolean addRecordDaka(RecordDaka recordDaka);

    /**
     * 获取用户打卡记录的日期
     * @param openid
     * @return
     */
    public LinkedList<String> getRecords(String openid);
}
