package com.ptit.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PracticeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PracticeServiceApplication.class, args);
	}

}
