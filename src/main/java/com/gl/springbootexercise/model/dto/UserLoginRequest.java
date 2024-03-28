package com.gl.springbootexercise.model.dto;

import lombok.Data;

@Data
public class UserLoginRequest {

    private String userAccount;

    private String password;
}
