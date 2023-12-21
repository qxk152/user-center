package com.qxk.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.qxk.usercenter.common.ErrorCode;
import com.qxk.usercenter.common.ResultUtils;
import com.qxk.usercenter.constant.UserConstant;
import com.qxk.usercenter.exception.BussinessException;
import com.qxk.usercenter.model.domain.User;
import com.qxk.usercenter.service.UserService;
import com.qxk.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author 86080
 * @description 针对表【originalUser(用户)】的数据库操作Service实现
 * @createDate 2023-10-31 22:50:16
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Autowired
    private UserMapper userMapper;
    private static final String SALT = "qxk";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String userPlanetCode) {
        //1.校验 看其中是否有空 null
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, userPlanetCode)) {
            throw new BussinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BussinessException(ErrorCode.PARAMS_ERROR, "账号长度必须不小于4");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BussinessException(ErrorCode.PARAMS_ERROR, "密码长度必须不小于8");
        }
        if (userPlanetCode.length() > 5) {
            throw new BussinessException(ErrorCode.PARAMS_ERROR, "星球长度必须不大于5");
        }

        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BussinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        //星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", userPlanetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BussinessException(ErrorCode.PARAMS_ERROR, "编号重复");
        }
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(userPlanetCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验 看其中是否有空 null
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BussinessException(ErrorCode.PARAMS_ERROR, "账号或密码不能为空");
        }
        if (userAccount.length() < 4) {
            throw new BussinessException(ErrorCode.PARAMS_ERROR, "账号长度必须不小于4");
        }
        if (userPassword.length() < 8) {
            throw new BussinessException(ErrorCode.PARAMS_ERROR, "密码长度必须不小于8");
        }

        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //是否有账户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("user login failed,user account can not match password");
            throw new BussinessException(ErrorCode.PARAMS_ERROR, "账户名或者密码错误");
        }
        //脱敏
        User saftyUser = getSaftyUser(user);

        //记录用户的登录态
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, saftyUser);
        return saftyUser;
    }

    /**
     * @return null
     * @author 86080
     * @description TODO
     * @date 2023/12/20 16:53
     */
    @Override
    public User getSaftyUser(User originalUser) {
        if (originalUser == null) {
            return null;
        }
        User saftyUser = new User();
        saftyUser.setId(originalUser.getId());
        saftyUser.setUsername(originalUser.getUsername());
        saftyUser.setUserAccount(originalUser.getUserAccount());
        saftyUser.setAvatarUrl(originalUser.getAvatarUrl());
        saftyUser.setGender(originalUser.getGender());
        saftyUser.setPhone(originalUser.getPhone());
        saftyUser.setPlanetCode(originalUser.getPlanetCode());
        saftyUser.setEmail(originalUser.getEmail());
        saftyUser.setUserStatus(originalUser.getUserStatus());
        saftyUser.setCreateTime(originalUser.getCreateTime());
        saftyUser.setUserRole(originalUser.getUserRole());
        saftyUser.setTags(originalUser.getTags());
        return saftyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        HttpSession session = request.getSession();

        session.removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }

    /**
     * @param tagNameList: 用户要拥有的标签
     * @return List<User>
     * @author 86080
     * @description TODO 利用内存进行查询
     * @date 2023/12/19 17:17
     */
    @Override
    public List<User> searchUserByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BussinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        /**
         采用将所有用户数据读入内存 再进行处理的方式
         */
        Gson gson = new Gson();
        //先查询所有的用户
        List<User> userList = userMapper.selectList(queryWrapper);
        //在内存中判断是否符合要求
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags(); //
//            if(StringUtils.isBlank(tagsStr)){
//                return false;
//            }
            //java无法通过Set.class获取类型信息
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            //tempTagNameSet可能为null 那么Optional 可以容纳为空指针的值，并且orElse 如果这个容器为空，就设置为传入的初始值
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(user -> getSaftyUser(user)).collect(Collectors.toList());


    }
    @Deprecated
    public List<User> searchUserByTagsBySql(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BussinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        //直接通过数据库查找符合标签的要求的用户
        for (String tagName : tagNameList) {
            //拼接like and  like 会自动加 "% tagName %"
            queryWrapper = queryWrapper.like("tags", tagName);

        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(user -> getSaftyUser(user)).collect(Collectors.toList());
//        userList.forEach(user -> {getSaftyUser(user);})
        //userList.forEach(this::getSaftyUser);
    }
}




