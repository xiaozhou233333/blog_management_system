package com.example.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data 
public class UpBlogParam {
    @NotNull (message = "Blog id can't be null")
    private Integer id;
    private String title;
    private String content;
}
