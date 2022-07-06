package xyz.ctstudy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户绿码申请记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Record implements Serializable {
    private int id;
    private String openid; //用户身份
    private int color; //申请时的健康码颜色
    private int reason;  //建康码颜色的原因 （7种情况）
    private String time; //申请的时间
    private String tripurl; //大数据行程码上传到云服务器的url
    private String reporturl;  //核酸报告上传到云服务器的url
    private int ischeck;  //是否被审核过（未审核0 拒绝1 通过2）
}
