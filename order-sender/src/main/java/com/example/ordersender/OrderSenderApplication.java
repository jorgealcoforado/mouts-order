package com.example.ordersender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.example.common.entity")
@EnableJpaRepositories(basePackages = "com.example.ordersender.repository")
public class OrderSenderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderSenderApplication.class, args);
    }
}
