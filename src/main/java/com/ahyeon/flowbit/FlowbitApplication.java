package com.ahyeon.flowbit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class FlowbitApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlowbitApplication.class, args);
    }

}
