package com.gl.springbootexercise.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gl.springbootexercise.annotation.AuthCheck;
import com.gl.springbootexercise.common.ErrorCode;
import com.gl.springbootexercise.common.Response;
import com.gl.springbootexercise.common.ResultUtils;
import com.gl.springbootexercise.constant.UserConstant;
import com.gl.springbootexercise.exception.BusinessException;
import com.gl.springbootexercise.mapper.QuestionMapper;
import com.gl.springbootexercise.model.dto.*;
import com.gl.springbootexercise.model.entity.Question;
import com.gl.springbootexercise.model.entity.QuestionSubmit;
import com.gl.springbootexercise.model.entity.User;
import com.gl.springbootexercise.model.vo.QuestionSubmitVO;
import com.gl.springbootexercise.model.vo.QuestionVO;
import com.gl.springbootexercise.model.vo.UserSubmitVO;
import com.gl.springbootexercise.model.vo.UserVO;
import com.gl.springbootexercise.service.QuestionService;
import com.gl.springbootexercise.service.QuestionSubmitService;
import com.gl.springbootexercise.service.UserService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/question")
@Slf4j
@CrossOrigin(origins = {"http://localhost:8080"},allowCredentials = "true")
public class QuestionController {
    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionSubmitService questionSubmitService;



    private final static Gson GSON = new Gson();

    /**
     *  添加题目
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public Response<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest,HttpServletRequest request){
        if(questionAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        return questionService.addQuestion(questionAddRequest,request);
    }

    /**
     * 删除题目
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public Response<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if(deleteRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        return questionService.deleteQuestion(deleteRequest,request);
    }

    /**
     * 更新题目（仅限管理员）
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public Response<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if(questionUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }

        return questionService.updateQuestion(questionUpdateRequest);
    }

    /**
     * 根据题号获取题目
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public Response<Question> getQuestionById(Long id, HttpServletRequest request) {
        if(id<0||id==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"id有问题");
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw  new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        Question question = questionService.getById(id);
        if(question == null){
            throw  new BusinessException(ErrorCode.SYSTEM_ERROR,"查询失败");
        }
        if(loginUser.getUserRole()!= UserConstant.USER_LOGIN_STATE && loginUser.getId()
        != question.getUserId()){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR , "无权限");
        }
        return ResultUtils.success(question);
    }

    /**
     * 根据 id 获取（脱敏）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public Response<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目不存在");
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public Response<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        if(size>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR , "请求参数错误");
        }
        Page<Question> questionPage = questionService.page(new Page<Question>(current,size), questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionPageVO(questionQueryRequest,questionPage,request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public Response<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        if(questionQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户未登录");
        }
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        if(size>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR , "请求参数错误");
        }
        Page<Question> questionPage = questionService.page(new Page<>(current, size), questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionPageVO(questionQueryRequest,questionPage,request));
    }

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public Response<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if(questionEditRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        //赋值
        Long id = questionEditRequest.getId();
        String title = questionEditRequest.getTitle();
        String content = questionEditRequest.getContent();
        List<String> tags = questionEditRequest.getTags();
        String answer = questionEditRequest.getAnswer();
        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest,question);
        if(!CollectionUtils.isEmpty(tags)){
            question.setTags(GSON.toJson(tags));
        }
        if(!CollectionUtils.isEmpty(judgeCase)){
            question.setTags(GSON.toJson(judgeCase));
        }
        if(judgeConfig != null){
            question.setTags(GSON.toJson(judgeConfig));
        }
        //仅管理员和用户本人可以修改
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        Question updateQuestion= questionService.getById(id);
        Long userId = updateQuestion.getUserId();
        if(userId!=loginUser.getId() && loginUser.getUserRole()!="admin"){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无权限");
        }
        boolean b = questionService.updateById(question);
        if(!b){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"存储失败");
        }
        return ResultUtils.success(true);
    }


@PostMapping("/question_submit/list/page")
public Response<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                     HttpServletRequest request) {
    long current = questionSubmitQueryRequest.getCurrent();
    long size = questionSubmitQueryRequest.getPageSize();
    // 从数据库中查询原始的题目提交分页信息
    Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
            questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
    final User loginUser = userService.getLoginUser(request);
    // 返回脱敏信息
    return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
}


    @PostMapping("/question_submit/do_submit")
    public Response<Long> doSubmit(@RequestBody QuestionSubmitRequest questionSubmitRequest , HttpServletRequest request){
        if(questionSubmitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        return questionSubmitService.doSubmit(questionSubmitRequest,loginUser);
    }

    @PostMapping("/question_submit/getAcceptRate")
    public Response<UserSubmitVO> getAcceptRate(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        return questionSubmitService.getAcceptRate(loginUser);
    }
}
