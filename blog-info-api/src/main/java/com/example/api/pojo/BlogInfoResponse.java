package com.example.api.pojo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data 
public class BlogInfoResponse {
    private Integer id;
    private String title;
    private String content;
    private Integer userId;

    @JsonFormat (pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat (pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
