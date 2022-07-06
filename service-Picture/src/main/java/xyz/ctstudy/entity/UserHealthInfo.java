package xyz.ctstudy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用于redis保存用户健康信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserHealthInfo implements Serializable {
    private String name; //姓名
    private String num;  // 学号
    private String classnum;  //班级号
    private int color; //当前建康码颜色（绿紫黄红）
    private int reason; //建康码颜色的原因有7种情况
    private String updatetime;  //上一次打卡的时间
    private int continuousnum; //截止上次打卡时，已连续打卡天数
}
