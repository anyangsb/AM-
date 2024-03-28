package com.gl.springbootexercise;

import com.gl.springbootexercise.mq.MessageProduct;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class SpringBootExerciseApplicationTests {

    @Resource
    private MessageProduct messageProduct;

    @Test
    void sendMessage(){
        messageProduct.sendMessage("code_exchange","routing_key","fucking");
    }

}
