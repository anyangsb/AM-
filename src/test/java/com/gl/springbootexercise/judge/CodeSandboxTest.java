package com.gl.springbootexercise.judge;


import com.gl.springbootexercise.judge.codesandbox.CodeSandBoxFactory;
import com.gl.springbootexercise.judge.codesandbox.CodeSandbox;
import com.gl.springbootexercise.judge.codesandbox.CodeSandboxProxy;
import com.gl.springbootexercise.judge.codesandbox.impl.ExampleCodeSandbox;
import com.gl.springbootexercise.judge.codesandbox.impl.RemoteCodeSandbox;
import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeRequest;
import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeResponse;
import com.gl.springbootexercise.model.enums.QuestionLanguageEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class CodeSandboxTest {

    @Value("${codesandbox.type:remote}")
    String type;
    @Test
    void executeCode() {
        CodeSandbox codeSandbox = new RemoteCodeSandbox();
        String code = "int i = 0;";
        String language = QuestionLanguageEnum.JAVA.getValue();
        List<String> inputList = new ArrayList<>();
        inputList.add("1 2");
        inputList.add("2 3");
        inputList.add("3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().code(code).language(language)
                .inputList(inputList).build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    @Test
    void executeCodeByValue() {
        CodeSandbox codeSandbox = CodeSandBoxFactory.newInstance(type);
        String code = "int i = 0;";
        String language = QuestionLanguageEnum.JAVA.getValue();
        List<String> inputList = new ArrayList<>();
        inputList.add("1 2");
        inputList.add("2 3");
        inputList.add("3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().code(code).language(language)
                .inputList(inputList).build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    @Test
    void executeCodeByValueAndProxy() {
        CodeSandbox codeSandbox = CodeSandBoxFactory.newInstance(type);
        CodeSandboxProxy codeSandboxProxy = new CodeSandboxProxy(codeSandbox);
        String code = "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(\"结果：\" + (a + b));\n" +
                "    }\n" +
                "}";
        String language = QuestionLanguageEnum.JAVA.getValue();
        List<String> inputList = new ArrayList<>();
        inputList.add("1 2");
        inputList.add("2 3");
        inputList.add("3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder().code(code).language(language)
                .inputList(inputList).build();
        ExecuteCodeResponse executeCodeResponse = codeSandboxProxy.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }
}