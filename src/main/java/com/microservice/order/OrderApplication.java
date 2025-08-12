package com.microservice.order;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderApplication {

	private static final Logger logger = LogManager.getLogger(OrderApplication.class);

	public static void main(String[] args) {
		logger.info("Starting Order Service Application...");
		logger.debug("Debug message from OrderApplication");
		logger.warn("Warning message from OrderApplication");
		logger.error("Error message from OrderApplication");

		try {
			SpringApplication.run(OrderApplication.class, args);
			logger.info("Order Service Application started successfully");
		} catch (Exception e) {
			// Check if it's a DevTools restart exception
			if (e.getClass().getName().contains("SilentExitException")) {
				logger.debug("DevTools restart detected - this is normal behavior");
			} else {
				logger.error("Failed to start Order Service Application", e);
				throw e;
			}
		}
	}

}
