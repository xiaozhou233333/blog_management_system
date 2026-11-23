package com.example.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.api.pojo.BlogInfoApi;
import com.example.api.pojo.BlogInfoResponse;
import com.example.api.pojo.UserInfoParam;
import com.example.api.pojo.UserInfoRegisterRequest;
import com.example.api.pojo.UserInfoResponse;
import com.example.api.pojo.UserLoginResponse;
import com.example.enums.ResultCodeEnum;
import com.example.exception.BlogException;
import com.example.mapper.UserInfoMapper;
import com.example.pojo.Constants;
import com.example.pojo.Result;
import com.example.pojo.UserInfo;
import com.example.utils.JSONUtils;
import com.example.utils.JWTUtils;
import com.example.utils.Redis;
import com.example.utils.RegexUtil;
import com.example.utils.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserInfoMapper userInfoMapper;

    private final BlogInfoApi blogInfoApi;

    private final Redis redis;

    private final RabbitTemplate rabbitTemplate;


    private final String USER_PREFIX = "user";
    private final long EXPIRE_TIME = 14 * 24 * 60 * 60 * 1000;

    @Override
    public UserLoginResponse login(UserInfoParam user) {
        UserInfo userInfo = queryUserInfo(user.getUserName());
        if (userInfo == null || userInfo.getId() == null) {
            throw new BlogException("The user does not exist");
        }
        if (!SecurityUtil.verify(user.getPassword(), userInfo.getPassword())) {
            throw new BlogException("password does not correct!");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userInfo.getId());
        claims.put("name", userInfo.getUserName());

        String jwt = JWTUtils.genJwt(claims);
        return new UserLoginResponse(userInfo.getId(), jwt);
    }

    @Override
    public UserInfoResponse getUserInfo(Integer userId) {
        UserInfo userInfo = selectUserInfoById(userId);
        if (userInfo == null) {
            throw new BlogException("The user does not exist");
        }
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        BeanUtils.copyProperties(userInfo, userInfoResponse);
        return userInfoResponse;
    }

    /**
     * 按 blogId 查作者。
     *
     * 两步走，注意顺序不能反：
     * 1. blogId 是 blog-service 的主键，user 库里查不到 → 先 Feign 调 /blog/getBlogDetail 拿 userId
     * 2. userId 才是 user 库的主键 → 复用 getUserInfo 查用户
     *
     * 之前这里直接把 blogId 当 user_info.id 查（还错用了 LambdaQueryWrapper&lt;UserInfoResponse&gt;），
     * 既编译不过，语义也是错的。
     */
    @Override
    public UserInfoResponse getAuthorInfo(Integer blogId) {
        if (blogId == null) {
            throw new BlogException("blogId can't be null!");
        }

        Result<BlogInfoResponse> blogResult = blogInfoApi.getBlogDetail(blogId);
        // 远程调用的失败也要当业务失败处理，不能让 null 一路传下去
        if (blogResult == null
            || blogResult.getCode() != ResultCodeEnum.SUCCESS.getCode()
            || blogResult.getData() == null) {
            throw new BlogException("Blog does not exist!");
        }

        Integer userId = blogResult.getData().getUserId();
        if (userId == null) {
            throw new BlogException("The author of this blog is unknown");
        }

        return getUserInfo(userId);
    }

    private UserInfo selectUserInfoByName(String userName) {
        return userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
            .eq(UserInfo::getUserName, userName)
            .eq(UserInfo::getDeleteFlag, 0));
    }

    private UserInfo queryUserInfo(String userName) {
        String key = buildKey(userName);
        Boolean exists = redis.hasKey(key);

        if (exists) {
            log.info("Getting data from redis, username: {}", userName);
            String userInfoJson = redis.get(buildKey(userName));
            return JSONUtils.parseObject(userInfoJson, UserInfo.class);
        } else {
            log.info("Getting data from mysql, username: {}", userName);
            return selectUserInfoByName(userName);
        }
    }

    private UserInfo selectUserInfoById(Integer userId) {
        return userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
            .eq(UserInfo::getId, userId)
            .eq(UserInfo::getDeleteFlag, 0));
    }

    @Override
    public Integer register(UserInfoRegisterRequest userInfoRegisterRequest) {
        checkUserInfo(userInfoRegisterRequest);

        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoRegisterRequest, userInfo);
        userInfo.setPassword(SecurityUtil.encrypt(userInfoRegisterRequest.getPassword()));
        try {
            int result = userInfoMapper.insert(userInfo);
            if (result > 0) {
                String content = JSONUtils.toJson(userInfo);
                redis.set(buildKey(userInfoRegisterRequest.getUserName()), content, EXPIRE_TIME);
                rabbitTemplate.convertAndSend(Constants.USER_EXCHANGE_NAME, "", content);
                
                return userInfo.getId();
            } else {
                throw new BlogException("user register failed!");
            }
        } catch (Exception e) {
            log.error("Exception happened while user register, e: ", e);
            throw new BlogException("user register failed!");
        }
    }

    private void checkUserInfo(UserInfoRegisterRequest param) {
        UserInfo userInfo = selectUserInfoByName(param.getUserName());
        if (userInfo != null) {
            throw new BlogException("The user is already exists!");
        }
        if (!RegexUtil.checkMail(param.getEmail())) {
            throw new BlogException("Email is not valid!");
        }
        if (!RegexUtil.checkUrl(param.getGithubUrl())) {
            throw new BlogException("URL is not valid!");
        }
    }

    private String buildKey(String userName) {
        return Redis.keyBuilder(USER_PREFIX, userName);
    }
}
