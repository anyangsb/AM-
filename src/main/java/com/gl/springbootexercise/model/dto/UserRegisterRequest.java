package com.gl.springbootexercise.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

//    /**
//     * 用户名
//     */
//    private String username;

    /**
     * 用户账户
     */
    private String userAccount;

    /**
     * 密码
     */
    private String password;

    /**
     * 确认密码
     */
    private String againPassword;

    private static final long serialVersionUID = 3191241716373120793L;
}
