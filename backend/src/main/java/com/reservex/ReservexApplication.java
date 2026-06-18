package com.reservex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ReservexApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReservexApplication.class, args);
	}

}
