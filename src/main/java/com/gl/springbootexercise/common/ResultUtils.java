package com.gl.springbootexercise.common;

public class ResultUtils {

    public static <T> Response<T> success(T data){
        return new Response(0,data,"success");
    }

    public static Response error(int code , String message){
        return new Response(code,message);
    }

}
