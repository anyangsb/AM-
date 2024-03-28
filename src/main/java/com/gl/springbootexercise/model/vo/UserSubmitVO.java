package com.gl.springbootexercise.model.vo;

import lombok.Data;

@Data
public class UserSubmitVO {
    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 题目总数
     */
    private Integer totalNum;
}
