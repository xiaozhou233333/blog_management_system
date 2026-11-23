package com.example.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.util.StringUtils;
// import org.springframework.web.context.request.RequestAttributes;
// import org.springframework.web.context.request.RequestContextHolder;
// import org.springframework.web.context.request.ServletRequestAttributes;

// import feign.RequestInterceptor;

/**
 * 把当前请求的 user_token 透传给下游服务。
 *
 * 已失效（C 方案：鉴权统一放在网关 AuthFilter）：
 * user-service 调 blog-service 时不再需要 token，所以去掉了 @Configuration。
 * 类保留，方便以后回滚。
 */
// public class FeignTokenForwardConfig {

//     @Bean
//     public RequestInterceptor userTokenForwardInterceptor() {
//         return template -> {
//             RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
//             if (attributes instanceof ServletRequestAttributes servletAttributes) {
//                 String token = servletAttributes.getRequest().getHeader("user_token");
//                 if (StringUtils.hasText(token)) {
//                     template.header("user_token", token);
//                 }
//             }
//         };
//     }
// }
