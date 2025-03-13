package com.fiap.GastroHub;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GastroHubApplication {
	private static final Logger logger = LogManager.getLogger(GastroHubApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GastroHubApplication.class, args);
		logger.info("GastroHub application started successfully!");
	}

}
