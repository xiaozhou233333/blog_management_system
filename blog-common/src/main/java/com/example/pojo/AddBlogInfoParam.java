package com.example.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddBlogInfoParam {
    @NotNull (message = "userId can't be null")
    private Integer userId;

    @NotBlank (message = "title can't be null")
    private String title;

    @NotBlank (message = "content can't be null")
    private String content;
}
