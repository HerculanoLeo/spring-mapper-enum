package com.herculanoleo.spring.me.sample;

import com.herculanoleo.spring.me.models.annotation.EnableMapperEnum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMapperEnum
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
