package com.utc.proyecto1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Proyecto1Application {
    public static void main(String[] args) {
        SpringApplication.run(Proyecto1Application.class, args);
        System.out.println("✅ Aplicación iniciada en: http://localhost:8080");
    }
}