package com.cdri.bookmng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BookMngApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookMngApplication.class, args);
    }

}
