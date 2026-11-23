package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * user 服务启动类。
 * 放在 com.example 根包，才能扫到 blog-common 里的拦截器 / 统一响应 / 全局异常。
 *
 * @EnableFeignClients 是课件要求加上去的：/user/getAuthorInfo?blogId= 需要调 blog-service 的契约。
 * 它同时也把本模块自己的 UserServiceApi 扫进来了，但服务端用不到它（那是给 blog-service 当客户端用的）。
 */
@SpringBootApplication(scanBasePackages = "com.example")
@EnableFeignClients(basePackages = "com.example.api")
@MapperScan("com.example.mapper")
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
