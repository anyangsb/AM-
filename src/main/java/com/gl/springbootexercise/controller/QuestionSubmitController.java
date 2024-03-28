package com.gl.springbootexercise.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gl.springbootexercise.common.Response;
import com.gl.springbootexercise.common.ResultUtils;
import com.gl.springbootexercise.model.dto.QuestionSubmitQueryRequest;
import com.gl.springbootexercise.model.entity.QuestionSubmit;
import com.gl.springbootexercise.model.entity.User;
import com.gl.springbootexercise.model.vo.QuestionSubmitVO;
import com.gl.springbootexercise.service.QuestionSubmitService;
import com.gl.springbootexercise.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

//@RestController
//@RequestMapping("/questionsubmit")
//public class QuestionSubmitController {
//    /**
//     * 分页获取题目提交列表（除了管理员外，其他普通用户只能看到非答案、提交的代码等公开信息）
//     *
//     * @param questionSubmitQueryRequest
//     * @param request
//     * @return
//     */
//
//    @Resource
//    private QuestionSubmitService questionSubmitService;
//
//    @Resource
//    private UserService userService;
//
////    @PostMapping("/list/page")
////    public Response<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
////                                                                     HttpServletRequest request) {
////        long current = questionSubmitQueryRequest.getCurrent();
////        long size = questionSubmitQueryRequest.getPageSize();
////        // 从数据库中查询原始的题目提交分页信息
////        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
////                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
////        final User loginUser = userService.getLoginUser(request);
////        // 返回脱敏信息
////        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
////    }
//}
