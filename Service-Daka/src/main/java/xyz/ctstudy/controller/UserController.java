package xyz.ctstudy.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.ctstudy.commons.Result;
import xyz.ctstudy.entity.HealthInfo;
import xyz.ctstudy.entity.User;
import xyz.ctstudy.entity.UserHealthInfo;
import xyz.ctstudy.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     * @param code
     * @return
     */
    @PostMapping("login")
    public Result<HashMap<String, Object>> login(String code){
        return userService.userLogin(code);
    }

    @GetMapping("getClassnums")
    public ArrayList<String> getClassnums(){
        return userService.getClassnums();
    }

    /**
     * 更新用户信息
     * @param user
     * @param openid
     * @return
     */
    @PostMapping("update")
    public Result update(User user,@RequestHeader("openid") String openid){
        user.setOpenid(openid);
        return userService.updateUser(user);
    }

    /**
     * 获取当前健康码颜色与原因
     * @param openid
     * @return
     */
    @GetMapping("color")
    public Result<HashMap<String, Object>> getColor(@RequestHeader("openid") String openid){
        HealthInfo healthInfo = userService.getHealthInfo(openid);
        if(healthInfo != null){
            HashMap<String, Object> map = new HashMap<>();
            map.put("color",healthInfo.getColor());
            map.put("reason",healthInfo.getReason());
            return Result.SUCCESS(map);
        }
        return Result.FAIL();
    }

    /**
     * 获取当前的健康信息
     * @param openid
     * @return
     */
    @RequestMapping("userHealthInfo")
    public UserHealthInfo userHealthInfo(@RequestBody String openid){
        return userService.getUserHealthInfo(openid);
    }

}
