package com.gl.springbootexercise.judge;

import cn.hutool.json.JSONUtil;
import com.gl.springbootexercise.common.ErrorCode;
import com.gl.springbootexercise.exception.BusinessException;
import com.gl.springbootexercise.judge.codesandbox.CodeSandBoxFactory;
import com.gl.springbootexercise.judge.codesandbox.CodeSandbox;
import com.gl.springbootexercise.judge.codesandbox.CodeSandboxProxy;
import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeRequest;
import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeResponse;
import com.gl.springbootexercise.judge.codesandbox.model.JudgeInfo;
import com.gl.springbootexercise.judge.strategy.JudgeContext;
import com.gl.springbootexercise.model.dto.JudgeCase;
import com.gl.springbootexercise.model.entity.Question;
import com.gl.springbootexercise.model.entity.QuestionSubmit;
import com.gl.springbootexercise.model.enums.QuestionSubmitStatusEnum;
import com.gl.springbootexercise.service.QuestionService;
import com.gl.springbootexercise.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;

    @Value("${codesandbox.type:example}")
    private String judgeType;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        //1.获取对应的信息
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if(questionSubmit == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"提交信息不存在");
        }
        Question question = questionService.getById(questionSubmit.getQuestionId());
        if(question == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"题目不存在");
        }
        //2.判断题目此时的状态，如果此时为等待中，就不用在执行了
        if(QuestionSubmitStatusEnum.WAITING.getValue().equals(questionSubmit.getStatus())){
            throw new BusinessException(ErrorCode.WAITING_ERROR,"题目正在判断");
        }
        //3.更新提交记录此时状态
        QuestionSubmit updateQS = new QuestionSubmit();
        updateQS.setId(questionSubmit.getId());
        updateQS.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmitService.updateById(updateQS);
        boolean update = questionSubmitService.updateById(updateQS);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        //4.沙盒执行代码
        CodeSandbox codeSandbox = CodeSandBoxFactory.newInstance(judgeType);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String judgeCase = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCase, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(s -> s.getInput()).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().code(questionSubmit.getCode()).language(questionSubmit.getLanguage())
                .inputList(inputList).build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();
        //5.判断执行得对不对
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = new JudgeManager().doJudge(judgeContext);
        //6、判题成功，将结果保存到数据库中
        String jsonStr = JSONUtil.toJsonStr(judgeInfo);
        updateQS.setJudgeInfo(jsonStr);
        updateQS.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        boolean b = questionSubmitService.updateById(updateQS);
        if(!b){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"存储失败");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitId);
        return updateQS;
    }
}
