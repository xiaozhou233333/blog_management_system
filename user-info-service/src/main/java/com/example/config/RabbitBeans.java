package com.example.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.pojo.Constants;

@Configuration 
public class RabbitBeans {
    @Bean ("userQueue")
    Queue userQueue() {
        return QueueBuilder.durable(Constants.USER_QUEUE).build();
    }

    @Bean ("userExchange")
    FanoutExchange fanoutExchange() {
        return ExchangeBuilder.fanoutExchange(Constants.USER_EXCHANGE_NAME).durable(true).build();
    }

    @Bean
    Binding queueBinding(@Qualifier ("userExchange") FanoutExchange exchange, @Qualifier ("userQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange);
    }
}
