package com.example.pojo;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

@Data 
public class UserInfo {
    @TableId (value = "id", type = IdType.AUTO)
    private Integer id;
    private String userName;
    private String password;
    private String githubUrl;
    private String email;
    private Byte deleteFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
