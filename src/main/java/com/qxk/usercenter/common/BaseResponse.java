package com.qxk.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 *  @description 通用返回类
 *  @author 86080
 *  @date 2023/11/10 23:36
 */
@Data
public class BaseResponse<T> implements Serializable {
    private int code;

    private T data;

    private String msg;

    private String description;
    public BaseResponse(int code, T data, String msg,String description) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.description = description;
    }
    public BaseResponse(int code, T data, String msg) {
       this(code,data,msg,"");
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDescription());
    }
    public BaseResponse(ErrorCode errorCode,String message,String description){
        this(errorCode.getCode(),null,message,description);
    }
    public BaseResponse(ErrorCode errorCode,String description){
        this(errorCode.getCode(),null,errorCode.getMessage(),description);
    }
}
