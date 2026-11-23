package com.example.pojo;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlogInfo {
    // 表里是 int AUTO_INCREMENT，缺这个注解时 MyBatis-Plus 会自己生成 64 位雪花 ID 再塞进 Integer，
    // 结果是被截断的随机数（实测插出来是 448704514），列表按 id 排序也就乱了
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @NotBlank (message = "title can't be null")
    private String title;
    @NotBlank (message = "content can't be null")
    private String content;
    private Integer userId;
    private Integer deleteFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
