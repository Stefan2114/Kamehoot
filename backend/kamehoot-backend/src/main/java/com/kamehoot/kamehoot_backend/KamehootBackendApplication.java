package com.kamehoot.kamehoot_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.kamehoot.kamehoot_backend.config.RsaKeyProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class KamehootBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(KamehootBackendApplication.class, args);
	}

}
