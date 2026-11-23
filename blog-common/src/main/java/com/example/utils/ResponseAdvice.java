package com.example.utils;

import org.springframework.http.MediaType;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.pojo.Result;

import lombok.SneakyThrows;
import tools.jackson.databind.ObjectMapper;

@ControllerAdvice 
public class ResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @SneakyThrows 
    @Override 
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof Result) {
            return body;
        }
        if (body instanceof String) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(Result.success(body));
        }
        return Result.success(body);
    }
}
