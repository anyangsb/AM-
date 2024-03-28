package com.gl.springbootexercise.judge.codesandbox.impl;

import com.gl.springbootexercise.judge.codesandbox.CodeSandbox;
import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeRequest;
import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 示例代码沙箱（仅为了跑通业务流程）
 *
 * @author Shier
 */
@Slf4j
public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }

}
