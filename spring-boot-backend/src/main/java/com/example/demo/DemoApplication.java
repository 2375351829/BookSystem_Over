package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.GraphicsEnvironment;

@SpringBootApplication
@MapperScan("com.example.demo.repository")
public class DemoApplication {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        GraphicsEnvironment.getLocalGraphicsEnvironment();
        SpringApplication.run(DemoApplication.class, args);
    }
}