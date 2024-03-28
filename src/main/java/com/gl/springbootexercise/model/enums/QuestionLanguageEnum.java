package com.gl.springbootexercise.model.enums;

public enum QuestionLanguageEnum {

    JAVA("java", "java"),
    CPLUSPLUS("cpp", "cpp"),
    GOLANG("go", "go");
    private String text;

    private String value;

    QuestionLanguageEnum(String text, String value){
        this.text = text;
        this.value = value;
    }

    public static QuestionLanguageEnum getEnumByValue(String value){
        if(value == null){
            return null;
        }
        for(QuestionLanguageEnum questionLanguageEnum : QuestionLanguageEnum.values()){
            if(questionLanguageEnum.getValue().equals(value)){
                return questionLanguageEnum;
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
