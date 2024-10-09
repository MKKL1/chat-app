package com.szampchat.server;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;

@EnableR2dbcRepositories
//https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations <- swagger annotation explained
@OpenAPIDefinition(
		info = @Info(
				title = "Szampchat server api",
				version = "0.1",
				description = ""
		),
		servers = {
				@Server(
						description = "Local server",
						url = "http://localhost:8081/api" //TODO insert port from configuration
				)
		}
)
@SpringBootApplication
public class SzampchatServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SzampchatServerApplication.class, args);
	}

}
