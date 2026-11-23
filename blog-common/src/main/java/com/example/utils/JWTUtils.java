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
    public static String secret = "dVnsmy+SIX6pNptQdeclDSJ26EMSPEIhvZYKBTTug4k=";
    public static long expiration = 24*60*60*1000;
    private static SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

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
