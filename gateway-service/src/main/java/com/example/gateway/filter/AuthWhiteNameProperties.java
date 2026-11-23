package com.example.gateway.filter;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data 
@Configuration 
@RefreshScope 
@ConfigurationProperties ("auth.white")
public class AuthWhiteNameProperties {
    private List<String> url;
}
