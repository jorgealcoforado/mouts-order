package com.example.orderquery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.orderquery", "com.example.common", "com.example.common.mapper"})
@EntityScan(basePackages = "com.example.common.entity")
@EnableJpaRepositories(basePackages = "com.example.orderquery.repository")
public class OrderQueryApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderQueryApplication.class, args);
    }
}
