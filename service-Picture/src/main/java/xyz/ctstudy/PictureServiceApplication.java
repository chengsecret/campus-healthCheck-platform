package xyz.ctstudy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient // Spring cloud Eureka 客户端，自动将本服务注册到 Eureka Server 注册中心中
@EnableFeignClients
@MapperScan("xyz.ctstudy.dao")
public class PictureServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PictureServiceApplication.class, args);
    }
}
