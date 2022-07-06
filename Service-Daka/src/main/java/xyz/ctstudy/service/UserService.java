package xyz.ctstudy.service;

import xyz.ctstudy.commons.Result;
import xyz.ctstudy.entity.HealthInfo;
import xyz.ctstudy.entity.User;
import xyz.ctstudy.entity.UserHealthInfo;

import java.util.ArrayList;
import java.util.HashMap;

public interface UserService {

    /**
     * 实现登录功能
     * @param code
     * @return
     */
    public Result<HashMap<String, Object>> userLogin(String code);

    /**
     * 跟新用户信息
     * @param user
     * @return
     */
    public Result updateUser(User user);

    /**
     * 获取所有的班级号
     * @return
     */
    public ArrayList<String> getClassnums();

    /**
     * 获取用户的健康信息
     * @param openid
     * @return
     */
    public HealthInfo getHealthInfo(String openid);

    public UserHealthInfo getUserHealthInfo(String openid);
}
