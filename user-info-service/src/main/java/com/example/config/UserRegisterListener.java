package com.example.config;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.pojo.Constants;
import com.example.pojo.UserInfo;
import com.example.utils.JSONUtils;
import com.rabbitmq.client.Channel;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Component 
@AllArgsConstructor 
public class UserRegisterListener {
    private final Mail mail;

    @RabbitListener (queues = Constants.USER_QUEUE)
    public void ListenerQueue(Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String body = new String(message.getBody());
            UserInfo userInfo = JSONUtils.parseObject(body, UserInfo.class);
            log.info("User register, userInfo: {}", body);
            if (userInfo.getEmail() != null) {
                String mailContent = new StringBuilder()
                    .append("hello" + userInfo.getUserName())
                    .append("thanks for registering our web!")
                    .append("username: " + userInfo.getUserName())
                    .append("have a nice day!").toString();
                mail.send(userInfo.getEmail(), "welcome to our website!", mailContent);
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
            log.error("failed to send the mail, e: {}", e);
        }
    }
}
