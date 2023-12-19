package com.qxk.usercenter.exception;

import com.qxk.usercenter.common.BaseResponse;
import com.qxk.usercenter.common.ErrorCode;
import com.qxk.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * TODO
 * <p>全局异常处理器
 *  @author 86080
 *  @date 2023/11/11 23:11
 *  @version 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BussinessException.class)
    public BaseResponse bussinessExceptionHandler(BussinessException e){
        log.error("BussinessExceptionHandler : " + e.getMessage());
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException: " + e.getMessage());
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(),"");
    }
}
