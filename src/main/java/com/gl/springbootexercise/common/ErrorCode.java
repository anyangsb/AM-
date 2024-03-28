package com.gl.springbootexercise.common;

public enum ErrorCode {

    SUCCESS(0,"成功"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN(40100,"未登录"),

    SYSTEM_ERROR(50000,"系统内部异常"),

    NO_AUTH_ERROR(40101,"无权限"),

    OPERATION_ERROR(40102,"题目正在判题中"),

    API_REQUEST_ERROR(50010,"调用API失败"),

    WAITING_ERROR(50020,"题目已在等待");

    private final int code;

    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
