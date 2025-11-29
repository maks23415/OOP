package com.example.lab5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.lab5.framework.repository")
public class Lab5Application {
    public static void main(String[] args) {
        SpringApplication.run(Lab5Application.class, args);
    }
}