package com.example.utils;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.exception.BlogException;
import com.example.pojo.Result;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RestControllerAdvice 
public class ExceptionAdvice {
    /**
     * 业务异常：返回给前端真实原因，不需要打印堆栈。
     */
    @ExceptionHandler(BlogException.class)
    public <T> Result<T> handleBlogException(BlogException e) {
        log.warn("Business error: {}", e.getMessage());
        return Result.fail(e.getMessage());
    }

    /**
     * 请求体校验失败：拼出可读的提示，避免把整段 Spring 异常信息返回给前端。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public <T> Result<T> handleInvalidArgument(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .filter(m -> m != null && !m.isBlank())
            .collect(Collectors.joining("; "));
        log.warn("Param verification failed: {}", message);
        return Result.fail(message.isEmpty() ? "Param verification failed!" : message);
    }

    /**
     * 静态资源/接口不存在：返回 404，不再每次打一整段堆栈（之前 favicon 404 就把日志刷满了）。
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNotFound(NoResourceFoundException e) {
        log.debug("Resource not found: {}", e.getResourcePath());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Result.fail("Resource not found: " + e.getResourcePath()));
    }

    @ExceptionHandler 
    public <T>Result<T> handler(Exception e) {
        log.error("Error happened: e:{}", e);
        return Result.fail(e.getMessage());
    }
}
