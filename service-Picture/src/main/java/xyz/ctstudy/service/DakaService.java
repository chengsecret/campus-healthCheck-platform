package xyz.ctstudy.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.ctstudy.entity.UserHealthInfo;


@Component
// 服务提供者提供的服务名称，即 application.name
@FeignClient(value = "ServiceOfDaka")
public interface DakaService {

    //接口中定义的每个方法都与服务提供者（即 service-daka）中 Controller 定义的服务方法对应。
    //DakaService这个接口就可以调用service-daka模块的controller了
    @RequestMapping("user/userHealthInfo")
    public UserHealthInfo userHealthInfo(String openid);
}
