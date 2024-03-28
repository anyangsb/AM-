package com.gl.springbootexercise.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gl.springbootexercise.common.Response;
import com.gl.springbootexercise.model.dto.QuestionSubmitQueryRequest;
import com.gl.springbootexercise.model.dto.QuestionSubmitRequest;
import com.gl.springbootexercise.model.entity.QuestionSubmit;
import com.gl.springbootexercise.model.entity.User;
import com.gl.springbootexercise.model.vo.QuestionSubmitVO;
import com.gl.springbootexercise.model.vo.UserSubmitVO;


/**
* @author 19328
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2023-11-02 20:22:53
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

    Response<Long> doSubmit(QuestionSubmitRequest questionSubmitRequest, User loginUser);

    Response<UserSubmitVO> getAcceptRate(User loginUser);
}
