package xyz.ctstudy.dao;

import org.springframework.stereotype.Repository;
import xyz.ctstudy.entity.Record;

@Repository
public interface RecordMapper {

    public Record getRecordByOenid(String openid);

    public boolean addRecord(Record record);

    public boolean updateRecord(Record record);
}
