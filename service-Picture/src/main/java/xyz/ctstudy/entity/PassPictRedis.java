package xyz.ctstudy.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassPictRedis implements Serializable {
    String date; //当天的redis
    String pictURL; //通行码在七牛云上的url
}
