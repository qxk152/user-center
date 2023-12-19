package com.qxk.usercenter.exception;

import com.qxk.usercenter.common.ErrorCode;

/**
 * TODO
 * <p>
 *  @author 86080
 *  @date 2023/11/11 19:43
 *  @version 1.0
 */
public class BussinessException extends RuntimeException{
    private int code;

    private String message;

    private String description;

    public BussinessException(int code,String message,String description){
        super(message);
        this.code = code;
        this.description = description;
    }
    public BussinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }
    public BussinessException(ErrorCode errorCode,String description){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
