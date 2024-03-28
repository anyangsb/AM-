package com.gl.springbootexercise.model.enums;

public enum QuestionDifficultyEnum {

    EASY("简单","简单"),

    MIDDLE("中等","中等"),

    DIFFICULT("困难","困难");

    private String text;
    private String value;


    QuestionDifficultyEnum(String text,String value){
        this.text = text;
        this.value = value;
    }

}
