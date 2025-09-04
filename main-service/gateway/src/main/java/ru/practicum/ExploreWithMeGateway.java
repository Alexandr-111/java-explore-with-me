package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"ru.practicum", "ru.practicum.config"})
public class ExploreWithMeGateway {
    public static void main(String[] args) {
        SpringApplication.run(ExploreWithMeGateway.class, args);
    }
}