package com.gl.springbootexercise.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.gl.springbootexercise.judge.codesandbox.model.JudgeInfo;
import com.gl.springbootexercise.model.dto.JudgeCase;
import com.gl.springbootexercise.model.dto.JudgeConfig;
import com.gl.springbootexercise.model.entity.Question;
import com.gl.springbootexercise.model.entity.QuestionSubmit;
import com.gl.springbootexercise.model.enums.JudgeInfoMessageEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * 默认判题策略
 */
public class JavaJudgeStrategy implements JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();


        Long memory = judgeInfo.getMemory();
        Long time = judgeInfo.getTime();
        //设置回复的属性
        JudgeInfo resJudgeInfo = new JudgeInfo();
        resJudgeInfo.setMemory(memory);
        resJudgeInfo.setTime(time);
        resJudgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
        //判断输入输出长度是否一致
        if(inputList.size()!=outputList.size()){
            resJudgeInfo.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getValue());
        }
        //若一致，则判断是否每一项的输出是否正确;
        if(!CollectionUtils.isEmpty(outputList)){
            for(int i = 0;i<outputList.size();i++){
                if(!outputList.get(i).equals(judgeCaseList.get(i).getOutput())){
                    resJudgeInfo.setMessage(JudgeInfoMessageEnum.WRONG_ANSWER.getValue());
                    return resJudgeInfo;
                }
            }
        }
        //若答案无误，则判断是否有超时
        String judgeConfig = question.getJudgeConfig();
        JudgeConfig bean = JSONUtil.toBean(judgeConfig, JudgeConfig.class);
        Long timeLimit = bean.getTimeLimit();
        Long memoryLimit = bean.getMemoryLimit();
        long JAVA_PROGRAM_TIME_COST = 10000L;
        if(timeLimit < time - JAVA_PROGRAM_TIME_COST){
            resJudgeInfo.setMessage(JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED.getValue());
            return resJudgeInfo;
        }
        if(memoryLimit < memory){
            resJudgeInfo.setMessage(JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED.getValue());
            return resJudgeInfo;
        }
        //若都没错，则说明答题正确;
        return resJudgeInfo;
    }
}
