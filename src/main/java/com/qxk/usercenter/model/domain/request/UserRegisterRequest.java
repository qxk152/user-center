package com.qxk.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * TODO
 * <p> 请求参数的javaBean
 *  @author 86080
 *  @date 2023/11/01 21:14
 *  @version 1.0
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -1680441438420259714L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

    private String planetCode;

}
