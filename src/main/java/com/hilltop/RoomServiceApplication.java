package com.hilltop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
//@EnableEurekaClient
public class RoomServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoomServiceApplication.class);
    }
}