package com.gl.springbootexercise.judge.codesandbox.impl;

import com.gl.springbootexercise.judge.codesandbox.CodeSandbox;
import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeRequest;
import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeResponse;
import com.gl.springbootexercise.judge.codesandbox.model.JudgeInfo;
import com.gl.springbootexercise.model.enums.JudgeInfoMessageEnum;
import com.gl.springbootexercise.model.enums.QuestionSubmitStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 示例代码沙箱（仅为了跑通业务流程）
 *
 * @author Shier
 */
@Slf4j
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setMemory(1000L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }

}
