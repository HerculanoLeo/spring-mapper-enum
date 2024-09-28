package com.herculanoleo.spring.me.sample;

import com.herculanoleo.spring.me.models.annotation.EnableFeignMapperEnum;
import com.herculanoleo.spring.me.models.annotation.EnableMapperEnum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableMapperEnum
@EnableFeignMapperEnum
@EnableFeignClients
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
