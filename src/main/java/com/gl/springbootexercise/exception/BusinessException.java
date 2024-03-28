package com.gl.springbootexercise.exception;

import com.gl.springbootexercise.common.ErrorCode;

public class BusinessException extends RuntimeException {

    private final int code;

    private String description;

    public int getCode() {
        return code;
    }

    public BusinessException(String message, int code) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode , String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
