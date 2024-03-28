package com.gl.springbootexercise.mq;

import com.gl.springbootexercise.exception.BusinessException;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageConsumer {

    /**
     * 指定程序监听的消息队列和确认机制
     *
     * @param message
     * @param channel
     * @param deliveryTag
     */
    @SneakyThrows
    @RabbitListener(queues = {"code_queue1"}, ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("接收到消息 ： {}", message);
        channel.basicAck(deliveryTag,false);
    }

}