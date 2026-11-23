package com.example.pojo;

import com.example.enums.ResultCodeEnum;

import lombok.Data;

@Data 
public class Result<T> {
    private int code;
    private String errMsg;
    private T data;

    public static <T>Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setData(data);
        return result;
    }

    public static <T>Result<T> fail(String errMsg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCodeEnum.FAIL.getCode());
        result.setErrMsg(errMsg);
        return result;
    }
}
