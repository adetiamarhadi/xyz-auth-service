package com.github.adetiamarhadi.xyz_auth_service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "Auth Service API",
				version = "v1",
				description = "API documentation for authentication service"
		)
)
@SpringBootApplication
public class XyzAuthServiceApplication {

	static void main(String[] args) {
		SpringApplication.run(XyzAuthServiceApplication.class, args);
	}

}
