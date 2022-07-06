package xyz.ctstudy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户健康情况
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthInfo implements Serializable {
    private int id;
    private String openid;
    private int color; //当前建康码颜色（绿紫黄红1234）
    private int reason; //建康码颜色的原因有6种情况(绿1、当天未打卡紫2、
                         // 位置为中高风险地区黄3、位置为省外黄4(弃用)、健康状况为疑似/确诊红5、浙江建康码非绿红6 )
    private String updatetime;  //上一次打卡的时间
    private int continuousnum; //截止上次打卡时，已连续打卡天数
}
