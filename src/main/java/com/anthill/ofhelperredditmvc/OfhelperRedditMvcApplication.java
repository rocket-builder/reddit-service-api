package com.anthill.ofhelperredditmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OfhelperRedditMvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(OfhelperRedditMvcApplication.class, args);
	}
}
