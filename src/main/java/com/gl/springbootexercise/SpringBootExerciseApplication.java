package com.gl.springbootexercise;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gl.springbootexercise.mapper")
public class SpringBootExerciseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootExerciseApplication.class, args);
    }

}
