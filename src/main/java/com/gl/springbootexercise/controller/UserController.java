package com.gl.springbootexercise.controller;

import com.gl.springbootexercise.common.ErrorCode;
import com.gl.springbootexercise.common.Response;
import com.gl.springbootexercise.common.ResultUtils;
import com.gl.springbootexercise.exception.BusinessException;
import com.gl.springbootexercise.model.dto.UserLoginRequest;
import com.gl.springbootexercise.model.dto.UserRegisterRequest;
import com.gl.springbootexercise.model.entity.User;
import com.gl.springbootexercise.model.vo.LoginUserVO;
import com.gl.springbootexercise.model.vo.UserVO;
import com.gl.springbootexercise.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpRequest;

@RestController
@RequestMapping("/user")
@Slf4j
@CrossOrigin(origins = {"http://localhost:8080"},allowCredentials = "true")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Response<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求为空");
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String password = userRegisterRequest.getPassword();
        String againPassword = userRegisterRequest.getAgainPassword();
        if (StringUtils.isAnyEmpty(userAccount, password, againPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }

        return userService.userRegister(userAccount, password, againPassword);
    }

    @PostMapping("/login")
    public Response<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求为空");
        }

        String userAccount = userLoginRequest.getUserAccount();
        String password = userLoginRequest.getPassword();

        if (StringUtils.isAnyEmpty(userAccount, password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        return userService.userLogin(userAccount, password,request);
    }

    @GetMapping("/get/login")
    public Response<UserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getUserVO(user));
    }
}
