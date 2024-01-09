package com.rawchen.javarun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JavaRunApplication {
	public static void main(String[] args) {
		SpringApplication.run(JavaRunApplication.class, args);
	}
}
