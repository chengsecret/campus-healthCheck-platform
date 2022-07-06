package xyz.ctstudy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 专业
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Major implements Serializable {
    private int id;
    private String name;
}
