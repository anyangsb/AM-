package com.gl.springbootexercise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gl.springbootexercise.common.ErrorCode;
import com.gl.springbootexercise.common.Response;
import com.gl.springbootexercise.common.ResultUtils;
import com.gl.springbootexercise.constant.CommonConstant;
import com.gl.springbootexercise.exception.BusinessException;
import com.gl.springbootexercise.judge.JudgeService;
import com.gl.springbootexercise.mapper.QuestionSubmitMapper;
import com.gl.springbootexercise.model.dto.QuestionSubmitQueryRequest;
import com.gl.springbootexercise.model.dto.QuestionSubmitRequest;
import com.gl.springbootexercise.model.entity.Question;
import com.gl.springbootexercise.model.entity.User;
import com.gl.springbootexercise.model.enums.QuestionLanguageEnum;
import com.gl.springbootexercise.model.enums.QuestionSubmitStatusEnum;
import com.gl.springbootexercise.model.vo.QuestionSubmitVO;
import com.gl.springbootexercise.model.vo.UserSubmitVO;
import com.gl.springbootexercise.service.QuestionSubmitService;
import com.gl.springbootexercise.service.UserService;
import com.gl.springbootexercise.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.gl.springbootexercise.model.entity.QuestionSubmit;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author 19328
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2023-11-02 20:22:53
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService {

    @Resource
    private UserService userService;

    @Lazy
    @Resource
    private QuestionServiceImpl questionService;
    @Lazy
    @Resource
    private JudgeService judgeService;

    @Override
        public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        //获取所有条件
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();
        //设置查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
//        queryWrapper.eq("isDelete")
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), CommonConstant.SORT_ORDER_ASC.equals(sortOrder),sortField);
        return queryWrapper;
    }

//    @Override
//    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
//        if(loginUser == null){
//            throw new BusinessException(ErrorCode.NOT_LOGIN , "未登录");
//        }
//        List<QuestionSubmit> records = questionSubmitPage.getRecords();
//        long current = questionSubmitPage.getCurrent();
//        long size = questionSubmitPage.getSize();
//        long total = questionSubmitPage.getTotal();
//        List<QuestionSubmitVO> questionSubmitVOList = records.stream().map(record -> QuestionSubmitVO.objToVo(record)).collect(Collectors.toList());
//
//        for(QuestionSubmitVO questionSubmitVO : questionSubmitVOList){
//            if(questionSubmitVO.getUserId() != loginUser.getId() && loginUser.getUserRole()!= UserConstant.ADMIN_ROLE){
//                questionSubmitVO.setCode("");
//            }
//        }
//        Page<QuestionSubmitVO> newPage = new Page<>(current, size, total);
//        newPage.setRecords(questionSubmitVOList);
//        return newPage;
//    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    @Override
    public Response<Long> doSubmit(QuestionSubmitRequest questionSubmitRequest, User loginUser) {
        String language = questionSubmitRequest.getLanguage();
        String code = questionSubmitRequest.getCode();
        Long questionId = questionSubmitRequest.getQuestionId();
        QuestionLanguageEnum languageEnum = QuestionLanguageEnum.getEnumByValue(language);
        if(languageEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有这种语言");
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(code);
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setUserId(loginUser.getId());
        questionSubmit.setQuestionId(questionId);


        boolean save = this.save(questionSubmit);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据库保存错误");
        }
        Long questionSubmitId = questionSubmit.getId();
        // 执行判题服务
        CompletableFuture.runAsync(() -> {
            judgeService.doJudge(questionSubmitId);
        });
        return ResultUtils.success(questionSubmit.getId());
    }

    @Override
    public Response<UserSubmitVO> getAcceptRate(User loginUser) {
        Long userId = loginUser.getId();
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        QueryWrapper<Question> queryWrapper1 = new QueryWrapper<>();
        UserSubmitVO userSubmitVO = new UserSubmitVO();
        //先得到题目总数
        int totalCount = questionService.count(queryWrapper1);
        userSubmitVO.setTotalNum(totalCount);
        //得到用户提交数
        queryWrapper.eq("userId",userId);
        userSubmitVO.setSubmitNum(this.count(queryWrapper));
        //得到用户通过的提交数;
        queryWrapper.eq("status",QuestionSubmitStatusEnum.SUCCEED.getValue());
        userSubmitVO.setAcceptedNum(this.count(queryWrapper));
        return ResultUtils.success(userSubmitVO);
    }
}




