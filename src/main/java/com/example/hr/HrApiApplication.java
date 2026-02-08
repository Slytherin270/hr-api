package com.example.hr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.modulith.Modulith;

@SpringBootApplication
@Modulith
@EnableCaching
public class HrApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(HrApiApplication.class, args);
    }
}
