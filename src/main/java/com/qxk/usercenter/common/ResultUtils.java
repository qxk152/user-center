package com.qxk.usercenter.common;

/**
 * TODO
 * <p>
 *  @author 86080
 *  @date 2023/11/10 23:41
 *  @version 1.0
 */
public class ResultUtils {
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"ok");
    }
    public static <T> BaseResponse<T> error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }
    public static <T> BaseResponse<T> error(ErrorCode errorCode,String message,String description){
        return new BaseResponse<>(errorCode,message,description);
    }
    public static <T> BaseResponse<T> error(ErrorCode errorCode,String description){
        return new BaseResponse<>(errorCode,description);
    }
    public static <T> BaseResponse<T> error(int errorCode,String message,String description){
        return new BaseResponse<>(errorCode,null,message,description);
    }
}
