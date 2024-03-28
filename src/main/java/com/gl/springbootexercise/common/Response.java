package com.gl.springbootexercise.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class Response<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public Response(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public Response(int code, String message) {
        this.code = code;
        this.data = null;
        this.message = message;
    }

    public Response(ErrorCode errorCode){
        this.code = errorCode.getCode();
        this.data = null;
        this.message = errorCode.getMessage();
    }
}
