package com.microservice.order.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	private static final Logger logger = LogManager.getLogger(RestTemplateConfig.class);

	@Bean
	public RestTemplate restTemplate() {
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(5_000, java.util.concurrent.TimeUnit.MILLISECONDS)
				.setResponseTimeout(5_000, java.util.concurrent.TimeUnit.MILLISECONDS)
				.build();

		CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build();

		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);
		return new RestTemplate(factory);
	}
//	@Bean
//	RestTemplate getRestTemplate() {
//		logger.info("Creating RestTemplate bean");
//		RestTemplate restTemplate = new RestTemplate();
//		logger.debug("RestTemplate bean created successfully");
//		return restTemplate;
//	}
	
	
}
