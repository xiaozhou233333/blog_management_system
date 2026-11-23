package com.example.utils;

// import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// import lombok.RequiredArgsConstructor;

/**
 * 原本在这里注册 LoginInterceptor 拦 /user/** 和 /blog/**。
 *
 * 按课件第 5 章的做法，鉴权已统一挪到网关 gateway-service 的 AuthFilter（GlobalFilter），
 * 服务内部不再做 token 校验。这里刻意去掉了 @Configuration：
 * 没有它，本类不会是 Bean，addInterceptors 也就永远不会被调用（拦截器自动失效）。
 *
 * 保留类体是为了随时能对比/回滚：把 @Configuration 加回来、去掉 @RequiredArgsConstructor 上的注释
 * 就能恢复服务内拦截的老行为。
 */
// @RequiredArgsConstructor
// public class AppConfig implements WebMvcConfigurer {
//     private final LoginInterceptor loginInterceptor;

//     @Override
//     public void addInterceptors(InterceptorRegistry registry) {
//         registry.addInterceptor(loginInterceptor)
//             .addPathPatterns("/user/**", "/blog/**")
//             .excludePathPatterns("/user/login");
//     }
// }
