package com.example.orderreceiver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.example.common.entity")
@EnableJpaRepositories(basePackages = "com.example.orderreceiver.repository")
public class OrderReceiverApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderReceiverApplication.class, args);
    }
}
