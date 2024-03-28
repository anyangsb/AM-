package com.gl.springbootexercise.judge;


import com.gl.springbootexercise.judge.codesandbox.model.JudgeInfo;
import com.gl.springbootexercise.judge.strategy.DefaultJudgeStrategy;
import com.gl.springbootexercise.judge.strategy.JavaJudgeStrategy;
import com.gl.springbootexercise.judge.strategy.JudgeContext;
import com.gl.springbootexercise.judge.strategy.JudgeStrategy;
import com.gl.springbootexercise.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}