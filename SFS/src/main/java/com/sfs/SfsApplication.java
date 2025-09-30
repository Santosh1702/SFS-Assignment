package com.sfs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.sfs.config.PlantConfig;

@SpringBootApplication
@EnableConfigurationProperties(PlantConfig.class)
public class SfsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SfsApplication.class, args);
	}

}
