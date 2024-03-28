package com.gl.springbootexercise.judge.codesandbox.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.gl.springbootexercise.common.ErrorCode;
import com.gl.springbootexercise.exception.BusinessException;
import com.gl.springbootexercise.judge.codesandbox.CodeSandbox;
import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeRequest;
import com.gl.springbootexercise.judge.codesandbox.model.ExecuteCodeResponse;
import com.gl.springbootexercise.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 示例代码沙箱（仅为了跑通业务流程）
 *
 * @author Shier
 */
@Slf4j
public class RemoteCodeSandbox implements CodeSandbox {

    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String url = "http://localhost:8090/executeCode";
        String jsonStr = JSONUtil.toJsonStr(executeCodeRequest);
        String res = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER,AUTH_REQUEST_SECRET)
                .body(jsonStr)
                .execute().body();
        if(StringUtils.isBlank(res)){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR , "调用API失败");
        }
        ExecuteCodeResponse executeCodeResponse = JSONUtil.toBean(res, ExecuteCodeResponse.class);
        return executeCodeResponse;
    }

}
