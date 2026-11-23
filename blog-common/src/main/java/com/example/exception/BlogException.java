package com.example.exception;

import com.example.pojo.Result;

public class BlogException extends RuntimeException {
    private Result<Object> errorResult = new Result<>();

    public BlogException(String message) {
        super(message);   // 必须把 message 传给父类，否则 e.getMessage() 为 null，前端拿不到错误原因
        this.errorResult.setCode(-1);
        this.errorResult.setErrMsg(message);
    }
}
