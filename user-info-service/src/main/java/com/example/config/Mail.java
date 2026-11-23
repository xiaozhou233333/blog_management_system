package com.example.config;

import java.util.Optional;

import org.springframework.boot.mail.autoconfigure.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;

public class Mail {
    private MailProperties mailProperties;
    private JavaMailSender javaMailSender;

    public Mail(MailProperties mailProperties, JavaMailSender javaMailSender) {
        this.mailProperties = mailProperties;
        this.javaMailSender = javaMailSender;
    }

    public void send(String to, String subject, String content) throws Exception {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false);

        String personal = Optional.ofNullable(mailProperties.getProperties().get("personal")).orElse(mailProperties.getUsername());

        helper.setFrom(mailProperties.getUsername(), personal);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        javaMailSender.send(message);
    }
}
