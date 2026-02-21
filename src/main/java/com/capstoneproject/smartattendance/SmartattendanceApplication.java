package com.capstoneproject.smartattendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SmartattendanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartattendanceApplication.class, args);
	}

}
