package xyz.ctstudy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 班级
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Classroom implements Serializable {
    private int id;
    private int mid; //所属的专业
    private String num; //班级号
}
