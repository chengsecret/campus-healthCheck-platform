package xyz.ctstudy.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import xyz.ctstudy.entity.HealthInfo;
import xyz.ctstudy.entity.User;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface UserMapper {
    /**
     * 添加用户
     * @param user
     * @return
     */
    public boolean addUser(User user);

    /**
     * 更新用户
     * @param user
     * @return
     */
    public boolean updateUser(User user);

    public boolean updateHealthInfo(HealthInfo healthInfo);

    /**
     * 添加健康信息
     */
    public boolean addHealthInfo(HealthInfo healthinfo);

    /**
     *根据openid查询用户
     */
    public User findUser(String openid);

    /**
     * 根据班级号查找用户
     * @param num
     * @return
     */
    public User findUserByNum(String num);

    /**
     * 根据通行码颜色和上一次打卡时间，查询用户的openid
     * @param color
     * @param updatetime
     * @return
     */
    public ArrayList<String> getHealthInfo(@Param("color") int color,@Param("updatetime") String updatetime);

    public boolean updateHealthInfoColorAndReason(@Param("color") int color,
                                                  @Param("updatetime") String updatetime,
                                                  @Param("newColor") int newColor,
                                                  @Param("reason") int reason);

    /**
     * 根据openid查询用户健康信息
     */
    public HealthInfo findHealthInfo(String openid);

    /**
     * 获得所有的班级号
     * @return
     */
    public ArrayList<String> getClassnums();

    public Integer getMid(@Param("num") String classNum);

}
