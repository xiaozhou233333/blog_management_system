package com.example.api.pojo;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 
public class UserInfoRegisterRequest {
    @NotBlank (message = "Username can't be null!")
    @Length (max = 20, message = "Username length can't be over 20")
    private String userName;

    @NotBlank (message = "Password can't be null!")
    @Length (max = 20, message = "Password length can't be over 20")
    private String password;

    @Length (max = 64, message = "GithubUrl length can't be over 20")
    private String githubUrl;

    @Length (max = 20, message = "Email length can't be over 20")
    private String email;
}
