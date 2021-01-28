package com.obby;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SimpleValidationApplication {

    private static final Logger logger = LoggerFactory.getLogger(SimpleValidationApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SimpleValidationApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() throws Exception {
        return args -> {

        };
    }

}
