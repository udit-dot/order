package com.microservice.order.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

	private static final Logger logger = LogManager.getLogger(ModelMapperConfig.class);

	@Bean
	public ModelMapper modelMapper() {
		logger.info("Creating ModelMapper bean");
		ModelMapper modelMapper = new ModelMapper();
		logger.debug("ModelMapper bean created successfully");
		return modelMapper;
	}
} 