package com.qxk.usercenter.service;

import com.qxk.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 86080
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-10-31 22:50:16
*/

public interface UserService extends IService<User> {

    /**
     * @param userAccount:用户账号
     * @param userPassword:用户密码
     * @param checkPassword:二次校验密码
     * @return long 新用户的 id
     * @author 86080
     * @description TODO
     * @date 2023/10/31 23:55
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String userPlanetCode);
    /**
     * @param userAccount:用户账号
     * @param userPassword:用户密码
     * @return 用户信息 （脱敏）
     * @author 86080
     * @description TODO
     * @date 2023/10/31 23:55
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest resquest);

    /**
     * @param originalUser:用户原始模型
     * @return 用户信息 （脱敏）
     * @author 86080
     * @description TODO
     * @date 2023/10/31 23:55
     */
    User getSaftyUser(User originalUser);
    /**
     * @param :
     * @author 86080
     * @description 用户注销
     * @return
     * @date 2023/11/10 16:54
     */
    int userLogout(HttpServletRequest request);
    /**
     * @param tagNameList: 传入的tag集合
      * @return List<User>
     * @author 86080
     * @description TODO 根据tag搜索满足tag的所有用户
     * @date 2023/12/19 17:15
     */
    List<User> searchUserByTags(List<String> tagNameList);
}
