package com.gl.springbootexercise.judge.codesandbox;

import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeRequest;
import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeResponse;

public interface CodeSandbox {

    ExecuteCodeResponse executeCode(ExecuteCodeRequest excecuteCodeRequest);
}
