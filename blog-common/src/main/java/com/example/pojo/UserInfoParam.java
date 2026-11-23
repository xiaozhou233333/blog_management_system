package com.example.pojo;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 
public class UserInfoParam {
    @NotBlank(message = "Username can't be blank!")
    @Length(max = 20, message = "Username can't be more than 20 letters!")
    private String userName;

    @NotBlank(message = "Password can't be blank!")
    @Length(max = 20, message = "Password can't be more than 20 letters!")
    private String password;
}
