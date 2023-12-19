package com.qxk.usercenter.common;

/**
 * TODO
 * <p>希望前端收到我们想给他发送的信息 所以对异常进行封装
 *  @author 86080
 *  @date 2023/11/11 00:01
 *  @version 1.0
 */
public enum ErrorCode {
    SUCCESS(0,"ok",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(40001,"请求数据为空",""),
    NOT_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"无权限",""),
    SYSTEM_ERROR(50000,"服务器内部异常","")
    ;

    /**
     * 状态码
     */
    private final int code;
    /**
     * 状态码描述
     */
    private final String message;
    /**
     * 状态码描述（详情）
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
