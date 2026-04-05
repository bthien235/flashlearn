package com.example.flashlearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlashlearnApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlashlearnApplication.class, args);
	}

}
