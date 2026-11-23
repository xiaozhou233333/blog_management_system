package com.example;

//import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.example.pojo.Constants;
import com.example.pojo.UserInfo;
import com.example.utils.JSONUtils;

import jakarta.mail.internet.MimeMessage;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.JavaVersion;

//@Disabled ("delete it after using test class")
@SpringBootTest 
public class ConditionalTest {
    @Autowired 
    private RabbitTemplate rabbitTemplate;

    @Autowired 
    JavaMailSender javaMailSender;

    @Test 
    void test() {
        System.out.println("Testing...");
    }

    @Test
    void test2() {
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword("");
        rabbitTemplate.convertAndSend(Constants.USER_EXCHANGE_NAME, "", JSONUtils.toJson(userInfo));
    }

    @Test
    public void test3() throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false);
        helper.setFrom("17347284726@163.com", "I'm xiaozhou!!!");
        helper.setTo("1262834916@qq.com");
        helper.setSubject("Hello");
        helper.setText("<h1>Hello</h1><p>world!</p>", true);
        javaMailSender.send(message);
    }
}

@Configuration 
class AppConfig {
    @Conditional (Java21Condition.class)
    @Bean 
    public JDK21 jdk21() {
        System.out.println("jdk21 initiallized!");
        return new JDK21();
    }
    @Conditional (Java17Condition.class)
    @Bean
    public JDK17 jdk17() {
        System.out.println("jdk17 initiallized!");
        return new JDK17();
    }
}

class JDK21 {}
class JDK17 {}

class Java21Condition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return JavaVersion.getJavaVersion().equals(JavaVersion.TWENTY_ONE);
    }
}

class Java17Condition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return JavaVersion.getJavaVersion().equals(JavaVersion.SEVENTEEN);
    }
}