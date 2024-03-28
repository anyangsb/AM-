package com.gl.springbootexercise.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gl.springbootexercise.common.Response;
import com.gl.springbootexercise.model.entity.User;
import com.gl.springbootexercise.model.vo.LoginUserVO;
import com.gl.springbootexercise.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;


/**
* @author 19328
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-10-30 22:00:26
*/
public interface UserService extends IService<User> {

    Response<Long> userRegister(String userAccount, String password, String againPassword);

    Response<UserVO> userLogin(String userAccount, String password, HttpServletRequest request);

    User getLoginUser(HttpServletRequest request);




    UserVO getUserVO(User user);


    boolean isAdmin(User loginUser);
}
