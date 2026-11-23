package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * blog 服务启动类。
 *
 * 关键点：
 * 1. 放在 com.example 根包并显式 scanBasePackages，否则扫不到 blog-common 里的
 *    AppConfig / LoginInterceptor / ResponseAdvice / ExceptionAdvice。
 * 2. @EnableFeignClients 指向 com.example.api，UserServiceApi 就在这个包。
 */
@SpringBootApplication(scanBasePackages = "com.example")
@EnableFeignClients(basePackages = "com.example.api")
@MapperScan("com.example.mapper")
public class BlogServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogServiceApplication.class, args);
    }
}
