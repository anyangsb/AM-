package com.gl.springbootexercise.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gl.springbootexercise.common.Response;
import com.gl.springbootexercise.model.dto.DeleteRequest;
import com.gl.springbootexercise.model.dto.QuestionAddRequest;
import com.gl.springbootexercise.model.dto.QuestionQueryRequest;
import com.gl.springbootexercise.model.dto.QuestionUpdateRequest;
import com.gl.springbootexercise.model.entity.Question;
import com.gl.springbootexercise.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;


/**
* @author 19328
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2023-11-02 20:23:02
*/
public interface QuestionService extends IService<Question> {

    Response<Long> addQuestion(QuestionAddRequest questionAddRequest, HttpServletRequest request);

    void validQuestion(Question question, boolean add);

    Response<Boolean> deleteQuestion(DeleteRequest deleteRequest, HttpServletRequest request);


    Response<Boolean> updateQuestion(QuestionUpdateRequest questionUpdateRequest);

    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    Page<QuestionVO> getQuestionPageVO(QuestionQueryRequest questionQueryRequest,Page<Question> questionPage, HttpServletRequest request);
}
