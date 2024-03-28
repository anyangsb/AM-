package com.gl.springbootexercise.judge.codesandbox;

import com.gl.springbootexercise.judge.codesandbox.impl.ExampleCodeSandbox;
import com.gl.springbootexercise.judge.codesandbox.impl.RemoteCodeSandbox;
import com.gl.springbootexercise.judge.codesandbox.impl.ThirdPartyCodeSandbox;

public class CodeSandBoxFactory{

    public static CodeSandbox newInstance(String type){
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
