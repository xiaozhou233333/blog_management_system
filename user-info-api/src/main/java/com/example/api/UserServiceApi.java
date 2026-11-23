package com.example.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.api.pojo.UserInfoParam;
import com.example.api.pojo.UserInfoRegisterRequest;
import com.example.api.pojo.UserInfoResponse;
import com.example.api.pojo.UserLoginResponse;
import com.example.pojo.Result;

import jakarta.validation.Valid;

/**
 * user-service 的调用契约（Feign 接口）。
 * 返回值统一 Result<T>，与服务端实际返回保持一致。
 */
@FeignClient(value = "user-service", url = "${services.user-service.url:}", path = "/user")
public interface UserServiceApi {

    @RequestMapping ("/login")
    Result<UserLoginResponse> login(@Validated @Valid @RequestBody UserInfoParam user);

    @RequestMapping ("/getUserInfo")
    Result<UserInfoResponse> getUserInfo(@RequestParam("userId") Integer userId);

    @RequestMapping ("/getAuthorInfo")
    Result<UserInfoResponse> getAuthorInfo(@RequestParam("blogId") Integer blogId);

    @RequestMapping ("/register")
    Result<Integer> register(@RequestBody UserInfoRegisterRequest userInfoRegisterRequest);
}
