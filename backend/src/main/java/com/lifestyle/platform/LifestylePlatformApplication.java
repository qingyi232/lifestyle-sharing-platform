package com.lifestyle.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lifestyle.platform.mapper")
public class LifestylePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifestylePlatformApplication.class, args);
    }
}
