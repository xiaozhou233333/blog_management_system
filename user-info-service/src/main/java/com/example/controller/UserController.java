package com.example.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.UserServiceApi;
import com.example.api.pojo.UserInfoParam;
import com.example.api.pojo.UserInfoRegisterRequest;
import com.example.api.pojo.UserInfoResponse;
import com.example.api.pojo.UserLoginResponse;
import com.example.pojo.Result;
import com.example.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController implements UserServiceApi {
    private final UserService userService;

    @RequestMapping("/login")
    public Result<UserLoginResponse> login(@Validated @Valid @RequestBody UserInfoParam user) {
        // 不要把密码写进日志
        log.info("User login: username={}", user.getUserName());
        return Result.success(userService.login(user));
    }

    @RequestMapping("/getUserInfo")
    public Result<UserInfoResponse> getUserInfo(@RequestParam("userId") Integer userId) {
        return Result.success(userService.getUserInfo(userId));
    }

    @RequestMapping("/getAuthorInfo")
    public Result<UserInfoResponse> getAuthorInfo(@RequestParam("blogId") Integer blogId) {
        log.info("getAuthorInfo, blogId: {}", blogId);
        return Result.success(userService.getAuthorInfo(blogId));
    }

    @Override
    @RequestMapping("/register")
    public Result<Integer> register(UserInfoRegisterRequest userInfoRegisterRequest) {
        log.info("User registered: {}", userInfoRegisterRequest);
        return Result.success(userService.register(userInfoRegisterRequest));
    }
}
