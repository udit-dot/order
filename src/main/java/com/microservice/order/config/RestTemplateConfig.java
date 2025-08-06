package com.microservice.order.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	private static final Logger logger = LogManager.getLogger(RestTemplateConfig.class);

	@Bean
	RestTemplate getRestTemplate() {
		logger.info("Creating RestTemplate bean");
		RestTemplate restTemplate = new RestTemplate();
		logger.debug("RestTemplate bean created successfully");
		return restTemplate;
	}
}
