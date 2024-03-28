package com.gl.springbootexercise.judge.codesandbox;

import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeRequest;
import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CodeSandboxProxy implements CodeSandbox{

    private CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest excecuteCodeRequest) {
        log.info("处理前信息" + codeSandbox.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(excecuteCodeRequest);
        log.info("处理后信息" + codeSandbox.toString());
        return executeCodeResponse;
    }
}
