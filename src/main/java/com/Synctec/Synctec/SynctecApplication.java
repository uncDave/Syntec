package com.Synctec.Synctec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SynctecApplication {

	public static void main(String[] args) {
		SpringApplication.run(SynctecApplication.class, args);
	}

}
