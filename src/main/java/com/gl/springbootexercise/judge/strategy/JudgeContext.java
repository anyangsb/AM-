package com.gl.springbootexercise.judge.strategy;


import com.gl.springbootexercise.judge.codesandbox.model.JudgeInfo;
import com.gl.springbootexercise.model.dto.JudgeCase;
import com.gl.springbootexercise.model.entity.Question;
import com.gl.springbootexercise.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
