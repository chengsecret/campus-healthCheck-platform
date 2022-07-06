package xyz.ctstudy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    private int id;
    private String openid; //微信传来的 身份信息
    private String name; //姓名
    private String num;  // 学号
    private String classnum;  //班级号
    private int mid;  // 专业
}
