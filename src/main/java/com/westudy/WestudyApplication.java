package com.westudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WestudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WestudyApplication.class, args);
	}

}
