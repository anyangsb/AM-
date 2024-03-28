package com.gl.springbootexercise.judge;

import com.gl.springbootexercise.model.entity.QuestionSubmit;

public interface JudgeService {

    /**
     * 判题模块
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);
}
