package xyz.ctstudy.service;

import xyz.ctstudy.commons.Result;
import xyz.ctstudy.entity.Record;

public interface RecordService {
    /**
     * 上条绿码申请是否被审核过
     * @param openid
     * @return
     */
    Result<Boolean> hasCheck(String openid);

    /**
     * 将图片上传到七牛云，获得url
     * 再将record保存到redis和数据库中
     * @param record
     * @param tripPictureBase64
     * @param tripType
     * @param reportPictureBase64
     * @param reportTpye
     * @return
     */
    Result apply(Record record, String tripPictureBase64, String tripType,
                                String reportPictureBase64, String reportTpye);
}
