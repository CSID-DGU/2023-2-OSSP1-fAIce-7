package com.example.cokkiri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CokkiriApplication {

	public static void main(String[] args) {
		SpringApplication.run(CokkiriApplication.class, args);
	}

}
