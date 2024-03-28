package com.gl.springbootexercise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gl.springbootexercise.common.ErrorCode;
import com.gl.springbootexercise.common.Response;
import com.gl.springbootexercise.common.ResultUtils;
import com.gl.springbootexercise.constant.CommonConstant;
import com.gl.springbootexercise.exception.BusinessException;
import com.gl.springbootexercise.mapper.QuestionMapper;

import com.gl.springbootexercise.model.dto.*;
import com.gl.springbootexercise.model.entity.Question;
import com.gl.springbootexercise.model.entity.QuestionSubmit;
import com.gl.springbootexercise.model.entity.User;
import com.gl.springbootexercise.model.vo.QuestionSubmitVO;
import com.gl.springbootexercise.model.vo.QuestionVO;
import com.gl.springbootexercise.model.vo.UserVO;
import com.gl.springbootexercise.service.QuestionService;
import com.gl.springbootexercise.service.QuestionSubmitService;
import com.gl.springbootexercise.service.UserService;
import com.gl.springbootexercise.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author 19328
* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2023-11-02 20:23:02
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService {

    @Resource
    private UserService userService;
    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionSubmitService questionSubmitService;


    private final static Gson GSON = new Gson();

    @Override
    public Response<Long> addQuestion(QuestionAddRequest questionAddRequest,HttpServletRequest request) {
        //拿到请求里的所有数据
        String title = questionAddRequest.getTitle();
        String content = questionAddRequest.getContent();
        List<String> tags = questionAddRequest.getTags();
        String answer = questionAddRequest.getAnswer();
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        //判断各个请求参数是否符合规定
        User user = userService.getLoginUser(request);
        Long userId = user.getId();
        Question question = new Question();
        question.setUserId(userId);
        BeanUtils.copyProperties(questionAddRequest,question);
        if(tags!=null){
            question.setTags(GSON.toJson(tags));
        }
        if(judgeCase!=null){
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        if(judgeConfig!=null){
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        this.validQuestion(question, false);
        boolean save = this.save(question);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据库存储失败");
        }
        return ResultUtils.success(question.getId());
    }

    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = question.getTitle();
        String content = question.getContent();
        String tags = question.getTags();
        String answer = question.getAnswer();
        String judgeCase = question.getJudgeCase();
        String judgeConfig = question.getJudgeConfig();
        // 创建时，参数不能为空
        if (add) {
            if(StringUtils.isAnyEmpty(title,content,tags)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
            }
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    @Override
    public Response<Boolean> deleteQuestion(DeleteRequest deleteRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        Long id = deleteRequest.getId();
        Question question = this.getById(id);
        if(question == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目不存在");
        }
        if(loginUser.getUserRole()!="admin"&&loginUser.getId()!=question.getUserId()){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无权限");
        }
        Boolean b = this.removeById(id);
        if(!b){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(b);
    }

    @Override
    public Response<Boolean> updateQuestion(QuestionUpdateRequest questionUpdateRequest) {
        Long id = questionUpdateRequest.getId();
        String title = questionUpdateRequest.getTitle();
        String content = questionUpdateRequest.getContent();
        List<String> tags = questionUpdateRequest.getTags();
        String answer = questionUpdateRequest.getAnswer();
        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if(id==null||id<0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目不存在");
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);

        if(tags!=null){
            question.setTags(GSON.toJson(tags));
        }
        if(judgeCase!=null){
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        if(judgeConfig!=null){
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        this.validQuestion(question, false);
        Boolean b = this.updateById(question);
        if(!b){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"存储失败");
        }
        return ResultUtils.success(b);
    }

    @Override
    public QuestionVO getQuestionVO(Question question, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"未登录");
        }
        QuestionVO questionVO = QuestionVO.objToVo(question);
        Long userId = question.getUserId();
        User user = userService.getById(userId);
        if(user == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"查询失败");
        }
        UserVO userVO = userService.getUserVO(user);
        questionVO.setUserVO(userVO);
        return questionVO;
    }

    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        if(questionQueryRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        Long id = questionQueryRequest.getId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        String answer = questionQueryRequest.getAnswer();
        Long userId = questionQueryRequest.getUserId();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        String difficulty = questionQueryRequest.getDifficulty();
        String status = questionQueryRequest.getStatus();
        //拼接查询条件
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(title) ,"title",title);
        queryWrapper.like(StringUtils.isNotBlank(content) ,"content",content);
        queryWrapper.like(StringUtils.isNotBlank(answer) ,"answer",answer);
        if(!"全部".equals(difficulty))
            queryWrapper.eq(StringUtils.isNotBlank(difficulty) ,"difficulty",difficulty);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        if(StringUtils.isNotBlank(status)&&!"全部".equals(status)){
            if(status.equals("未开始")){
                queryWrapper.notExists("Select 1 from question_submit where questionId = question.id");
            }else if(status.equals("已解答")){
                queryWrapper.exists("Select 1 from question_submit where questionId = question.id and status = 2");
            }else if(status.equals("尝试过")){
                queryWrapper.exists("Select 1 from question_submit where questionId = question.id and status = 3");
            }
        }
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        if(CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags","\"" + tag + "\"");
            }
        }
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),sortOrder.equals(CommonConstant.SORT_ORDER_ASC),sortField);
        return queryWrapper;
    }

    @Override
    public Page<QuestionVO> getQuestionPageVO(QuestionQueryRequest questionQueryRequest,Page<Question> questionPage, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN , "未登录");
        }
        List<Question> questionPageRecords = questionPage.getRecords();
        Page<QuestionVO> questionVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        long oldTotal = questionVOPage.getTotal();
        if (CollectionUtils.isEmpty(questionPageRecords)) {
            return questionVOPage;
        }
        //查询关联用户的信息;
        Set<Long> userIdSet = questionPageRecords.stream().map(Question::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        List<QuestionVO> questionVOList = questionPageRecords.stream().map(question -> {
            QuestionVO questionVO = QuestionVO.objToVo(question);
            Long questionId = question.getId();
            QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("questionId", questionId);
            queryWrapper.orderByDesc("createTime");
            List<QuestionSubmit> questionSubmitList = questionSubmitService.list(queryWrapper);
            if(questionSubmitList.size() > 0){
                if(questionSubmitList.get(0).getStatus() == 2){
                    questionVO.setStatus("已解答");
                }else if(questionSubmitList.get(0).getStatus() == 3){
                    questionVO.setStatus("尝试过");
                }
            }else{
                questionVO.setStatus("未开始");
            }
            User user = null;
            Long userId = question.getUserId();
            if (userIdMap.containsKey(userId)) {
                user = userIdMap.get(userId).get(0);
            }
            questionVO.setUserVO(userService.getUserVO(user));
            return questionVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(questionVOList);
        return questionVOPage;
    }

//    public List<Question> filterQuestionPageByStatus(QuestionQueryRequest questionQueryRequest,List<Question> questionPageRecords, User loginUser){
//        Long userId = loginUser.getId();
//        if(questionQueryRequest.getStatus().equals("未开始")){
//            questionPageRecords = questionPageRecords.stream().map(question -> {
//                QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
//                Long id = question.getId();
//                queryWrapper.eq("questionId", id);
//                queryWrapper.eq("userId",userId);
//                int count = questionSubmitService.count(queryWrapper);
//                if (count > 0) {
//                    return null;
//                }
//                return question;
//            }).filter(u -> u != null).collect(Collectors.toList());
//        }else if(questionQueryRequest.getStatus().equals("已解答")){
//            questionPageRecords = questionPageRecords.stream().map(question -> {
//                QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
//                Long id = question.getId();
//                queryWrapper.eq("questionId", id);
//                queryWrapper.eq("userId",userId);
//                queryWrapper.eq("status",2);
//                int count = questionSubmitService.count(queryWrapper);
//                if (count==0) {
//                    return null;
//                }
//                return question;
//            }).filter(u -> u != null).collect(Collectors.toList());
//        }else{
//            questionPageRecords = questionPageRecords.stream().map(question -> {
//                QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
//                Long id = question.getId();
//                queryWrapper.eq("questionId", id);
//                queryWrapper.eq("userId",userId);
//                queryWrapper.eq("status",3);
//                int count = questionSubmitService.count(queryWrapper);
//                if (count==0) {
//                    return null;
//                }
//                return question;
//            }).filter(u -> u != null).collect(Collectors.toList());
//        }
//        return questionPageRecords;
//    }
}




