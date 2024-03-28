package com.gl.springbootexercise.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MqInitMain {
    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String EXCHANGE_NAME = "code_exchange";
            channel.exchangeDeclare(EXCHANGE_NAME,"direct");

            //
            String QUEUE_NAME = "code_queue1";
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"routing_key");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
