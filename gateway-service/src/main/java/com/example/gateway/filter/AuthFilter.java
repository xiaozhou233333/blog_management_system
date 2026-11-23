package com.example.gateway.filter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

/**
 * 网关统一鉴权（课件第 5.2 节的做法）。
 *
 * 原来这件事由各服务里的 LoginInterceptor 做，问题是：
 * 1. 每个服务都要注册一遍，新增服务容易漏；
 * 2. 服务之间 Feign 互调时还得靠 RequestInterceptor 一层层透传 user_token。
 * 挪到网关后，鉴权只有一个入口，服务内部不再关心 token。
 *
 * 白名单来自 application.yml 的 auth.whitelist（逗号分隔），默认只放登录。
 * token 用与 blog-common 的 JWTUtils 相同的密钥 + 算法（HS256）校验，
 * 签名密钥配在 jwt.secret（值来自环境变量 JWT_SECRET），必须和 JWTUtils 用的一致。
 */
@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper;
    private final byte[] secretBytes;
    private final AuthWhiteNameProperties authWhiteName;

    public AuthFilter(ObjectMapper objectMapper, @Value("${jwt.secret}") String jwtSecret, AuthWhiteNameProperties authWhiteName) {
        this.objectMapper = objectMapper;
        this.secretBytes = Decoders.BASE64.decode(jwtSecret);
        this.authWhiteName = authWhiteName;
    }

    private boolean match(String url, List<String> urls) {
        if (CollectionUtils.isEmpty(urls)) {
            return false;
        }
        return urls.contains(url);
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 白名单直接放行
        if (match(path, authWhiteName.getUrl())) {
            return chain.filter(exchange);
        }

        String userToken = request.getHeaders().getFirst("user_token");
        // 不要把完整 token 打进日志，只留前 12 位方便排查
        log.info("Got token from header: {}", mask(userToken));

        if (!StringUtils.hasLength(userToken)) {
            return unauthorized(exchange, "令牌不能为空");
        }

        Integer userId = getUserIdFromToken(userToken);
        if (userId == null) {
            return unauthorized(exchange, "令牌已过期或者不合法");
        }
        return chain.filter(exchange);
    }

    private Integer getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretBytes))
                .build()
                .parseClaimsJws(token)
                .getBody();
            Object id = claims.get("id");
            return id instanceof Number number ? number.intValue() : null;
        } catch (Exception e) {
            // 过期 / 签名不对 / 格式错都归到这里
            log.warn("Failed to parse token: {}", e.getMessage());
            return null;
        }
    }

    @SneakyThrows
    private Mono<Void> unauthorized(ServerWebExchange exchange, String msg) {
        log.error("[用户身份认证异常] 请求路径: {}", exchange.getRequest().getPath());
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        // 网关侧不能依赖 blog-common 的 Result，这里手拼一个同结构的响应
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", -1);
        body.put("errMsg", msg);
        body.put("data", null);

        DataBuffer buffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(body));
        return response.writeWith(Mono.just(buffer));
    }

    private String mask(String token) {
        if (token == null) {
            return "null";
        }
        return token.length() <= 12 ? "***" : token.substring(0, 12) + "...";
    }

    /** 值越小优先级越高，比路由过滤器先执行 */
    @Override
    public int getOrder() {
        return -200;
    }
}
