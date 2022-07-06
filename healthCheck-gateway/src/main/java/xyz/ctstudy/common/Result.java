package xyz.ctstudy.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code = 0;
    private String message = "ok";
    private T data;
}
