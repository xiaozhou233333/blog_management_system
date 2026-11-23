package com.example.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Component 
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwtToken = request.getHeader("user_token");
        log.info("Got token from header: {}", jwtToken);
        Claims claims = JWTUtils.parseJWT(jwtToken);
        if (claims != null) {
            log.info("Token confirmed!");
            return true;
        }
        response.setStatus(401);
        return false;
    }
}
