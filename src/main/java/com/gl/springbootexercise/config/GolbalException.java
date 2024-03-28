package com.gl.springbootexercise.config;

import com.gl.springbootexercise.common.ErrorCode;
import com.gl.springbootexercise.common.Response;
import com.gl.springbootexercise.common.ResultUtils;
import com.gl.springbootexercise.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Configuration
@RestControllerAdvice
@Slf4j
public class GolbalException {

    @ExceptionHandler(BusinessException.class)
    public Response<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }
    @ExceptionHandler(RuntimeException.class)
    public Response runtimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统错误");
    }

}
