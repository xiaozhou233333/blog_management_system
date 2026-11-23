package com.example.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
public class JWTUtils {
    /**
     * 签名密钥不再写死在源码里：从环境变量 JWT_SECRET 读，读不到再试 -Djwt.secret=...
     * 要求 Base64 编码、解码后至少 32 字节（HS256）。
     * 网关的 jwt.secret 也必须指向同一个环境变量，否则两边验签对不上。
     */
    public static String secret = resolveSecret();
    public static long expiration = 24*60*60*1000;
    private static SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

    private static String resolveSecret() {
        String value = System.getenv("JWT_SECRET");
        if (!StringUtils.hasLength(value)) {
            value = System.getProperty("jwt.secret");
        }
        if (!StringUtils.hasLength(value)) {
            throw new IllegalStateException("未配置 JWT 签名密钥：请设置环境变量 JWT_SECRET，"
                + "或启动参数 -Djwt.secret=...（Base64 编码，解码后至少 32 字节）。"
                + "网关的 jwt.secret 必须与之相同。");
        }
        return value;
    }

    public static String genJwt(Map<String, Object> claim) {
        String jwt = Jwts.builder()
            .setClaims(claim)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(secretKey)
            .compact();
        
        return jwt;
    }

    public static Claims parseJWT(String jwt) {
        if (!StringUtils.hasLength(jwt)) {
            return null;
        }
        JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder().setSigningKey(secretKey);
        Claims claims = null;

        try {
            claims = jwtParserBuilder.build().parseClaimsJws(jwt).getBody();
        } catch (Exception e) {
            log.error("Failed to analyze token: jwt: {}", jwt);
        }
        return claims;
    }

    public static Integer getUserIdFromToken(String jwtToken) {
        Claims claims = JWTUtils.parseJWT(jwtToken);
        if (claims != null) {
            Map<String, Object> userInfo = new HashMap<>(claims);
            return (Integer) userInfo.get("id");
        }
        return null;
    }
}
