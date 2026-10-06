package net.talaatharb.healthcatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class HealthCatalogApplication {
	
	public static void main(String[] args) {
		log.trace("Application Started");

		SpringApplication.run(HealthCatalogApplication.class, args);
		
		log.trace("Application finished");
	}
}