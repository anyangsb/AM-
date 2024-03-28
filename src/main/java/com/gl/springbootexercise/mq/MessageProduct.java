package com.gl.springbootexercise.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MessageProduct {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routeKey, String message){
        rabbitTemplate.convertAndSend(exchange,routeKey,message);
    }

}
