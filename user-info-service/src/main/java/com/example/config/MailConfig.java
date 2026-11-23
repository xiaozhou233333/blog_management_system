package com.example.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration 
@ConditionalOnProperty (
    prefix = "spring.mail",
    name = "host"
)
public class MailConfig {
    @Bean 
    Mail mail(MailProperties mailProperties, JavaMailSender mailSender) {
        return new Mail(mailProperties, mailSender);
    }
}
