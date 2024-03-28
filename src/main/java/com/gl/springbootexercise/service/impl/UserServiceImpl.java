package com.gl.springbootexercise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gl.springbootexercise.common.ErrorCode;
import com.gl.springbootexercise.common.Response;
import com.gl.springbootexercise.common.ResultUtils;
import com.gl.springbootexercise.exception.BusinessException;
import com.gl.springbootexercise.mapper.UserMapper;
import com.gl.springbootexercise.model.entity.User;
import com.gl.springbootexercise.model.enums.UserRoleEnum;
import com.gl.springbootexercise.model.vo.LoginUserVO;
import com.gl.springbootexercise.model.vo.UserVO;
import com.gl.springbootexercise.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static com.gl.springbootexercise.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author 19328
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-10-30 22:00:26
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    private static final String SALT = "GL";
    @Override
    public Response<Long> userRegister(String userAccount, String password, String againPassword) {
        //判断用户名长度符不符合固定
        if(userAccount.length() >12||userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度不符合");
        }
        if(password.length()<4||password.length()>12){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不符合");
        }
        if(!password.equals(againPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码长度不一致");
        }
        //判断用户是否注册过
        synchronized (userAccount.intern()) {
            //若存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            int count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账户已存在");
            }
            //若不存在，则注册成功
            //将密码加密
            String encryPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
            User user = new User();
            user.setUserAccount(userAccount);
            user.setPassword(encryPassword);
            //若注册失败，抛出异常
            boolean b = this.save(user);
            if(!b){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"保存错误，数据库异常");
            }
            return ResultUtils.success(user.getId());
        }

    }

    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }
    @Override
    public Response<UserVO> userLogin(String userAccount, String password, HttpServletRequest request) {
        //判断账号密码是否服符合要求
        if(userAccount.length() >12||userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度不符合");
        }
        if(password.length()<4||password.length()>12){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度不符合");
        }
        //判断是否正确
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        String encryPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        queryWrapper.eq("password",encryPassword);
        User user= userMapper.selectOne(queryWrapper);
        if(user==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或密码有误");
        }
        //登录成功，返回脱敏用户;
        UserVO userVO = getSaferUser(user);
        request.getSession().setAttribute(USER_LOGIN_STATE,user);
        return ResultUtils.success(userVO);
    }

    public UserVO getSaferUser(User user){
        if(user==null)
            return null;
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return userVO;
    }

    @Override
    public User getLoginUser(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        if(user==null||user.getId()==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }
        long userId = user.getId();
        User  currUser= this.getById(userId);
        if (currUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return currUser;
    }




    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}




