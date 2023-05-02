package com.ssg.webpos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class WebposApplication {
	public static void main(String[] args) {
		System.out.println("WebposApplication.main-before");
		SpringApplication.run(WebposApplication.class, args);
		System.out.println("WebposApplication.main-after");
	}
}
