package com.example.service;

import com.example.api.pojo.UserInfoParam;
import com.example.api.pojo.UserInfoRegisterRequest;
import com.example.api.pojo.UserInfoResponse;
import com.example.api.pojo.UserLoginResponse;

public interface UserService {
    UserLoginResponse login(UserInfoParam user);

    UserInfoResponse getUserInfo(Integer userId);

    UserInfoResponse getAuthorInfo(Integer blogId);

    Integer register(UserInfoRegisterRequest userInfoRegisterRequest);
}
