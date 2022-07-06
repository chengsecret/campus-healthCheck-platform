package xyz.ctstudy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 打卡记录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordDaka implements Serializable {
    private int id;
    private String openid;
    private int state; //健康状况（三种情况：良好0 疑似/密接1 确诊2）
    private int zjpicture; //浙江建康码颜色（无0红1黄2绿3）
    private String time; //打卡时间
    private double longitude; //经度
    private double latitude; //纬度
    private int isrisk;  //是否为风险地区（1是 0不是）
}
