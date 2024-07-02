package com.szampchat.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcRepositories
@SpringBootApplication
public class SzampchatServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SzampchatServerApplication.class, args);
	}

}
