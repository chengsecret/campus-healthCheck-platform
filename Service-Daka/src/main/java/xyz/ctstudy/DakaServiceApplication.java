package xyz.ctstudy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableEurekaClient // Spring cloud Eureka 客户端，自动将本服务注册到 Eureka Server 注册中心中
@EnableScheduling //开启基于注解的定时任务
@MapperScan("xyz.ctstudy.dao")
public class DakaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DakaServiceApplication.class, args);
    }
}
