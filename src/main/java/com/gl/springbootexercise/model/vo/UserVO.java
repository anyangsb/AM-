package com.gl.springbootexercise.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVO {

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户账户
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;


    /**
     * 用户身份
     */
    private String userRole;
    /**
     * 用户简洁
     */
    private String userProfile;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}
