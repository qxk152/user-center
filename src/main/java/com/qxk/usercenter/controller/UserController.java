package com.qxk.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qxk.usercenter.common.BaseResponse;
import com.qxk.usercenter.common.ErrorCode;
import com.qxk.usercenter.common.ResultUtils;
import com.qxk.usercenter.constant.UserConstant;
import com.qxk.usercenter.exception.BussinessException;
import com.qxk.usercenter.model.domain.User;
import com.qxk.usercenter.model.domain.request.UserRegisterRequest;
import com.qxk.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO
 * <p>
 *  @author 86080
 *  @date 2023/11/01 20:53
 *  @version 1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            return null;
        }
        long id = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        return ResultUtils.success(id);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserRegisterRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            throw new BussinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword,request);
//        return new BaseResponse<>(0,user,"ok");
        return ResultUtils.success(user);
    }
    @PostMapping("/logout")
    public BaseResponse<Integer>  userLogout(HttpServletRequest request){
        if(request == null){
            return null;
        }
        int res = userService.userLogout(request);
        return  ResultUtils.success(res);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest request){
        //鉴权 管理员才能搜索
        if(!isAdmin(request)){
            return null;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSaftyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(Long id,HttpServletRequest request){
        //鉴权 管理员才能搜索
        if(!isAdmin(request)){
            return null;
        }
        if(id <= 0){
            return null;
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User curUser = (User) userObj; //空值可以强转但是不能调用方法
        if(curUser == null){
            throw new BussinessException(ErrorCode.NOT_LOGIN);
        }
        //由于用户的信息可能不断在变化 所以最好是直接去查数据库
        Long userId = curUser.getId();
        //todo 检验用户是否合法
        User userById = userService.getById(userId);
        User saftyUser = userService.getSaftyUser(userById);
        return ResultUtils.success(saftyUser);
    }
    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj; //空值可以强转但是不能调用方法
        if(user == null || user.getUserRole() != UserConstant.USER_ROLE_ADMIN){
            return false;
        }
        return true;
    }
}
