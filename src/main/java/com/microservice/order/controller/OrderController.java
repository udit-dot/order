package com.microservice.order.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.order.dto.OrderDto;
import com.microservice.order.dto.UserDto;
import com.microservice.order.service.OrderService;
import com.microservice.order.util.LoggingUtil;

@RestController
@RequestMapping("/orders")
public class OrderController {

	private static final Logger logger = LogManager.getLogger(OrderController.class);

	@Autowired
	private OrderService orderService;

	@GetMapping("/{id}")
	public ResponseEntity<OrderDto> getOrderDetails(@PathVariable Integer id) {
		logger.info("Received request to get order details for ID: {}", id);

		try {
			OrderDto dto = orderService.getDetails(id);
			logger.info("Successfully retrieved order details for ID: {}", id);
			return ResponseEntity.ok(dto);
		} catch (Exception e) {
			logger.error("Error occurred while retrieving order details for ID: {}", id, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping("/placeOrder")
	public ResponseEntity<OrderDto> placeOrder(@RequestBody OrderDto orderDto) {
		logger.info("Received request to place order: {}", orderDto);

		try {
			OrderDto placedOrder = orderService.placeOrder(orderDto);
			logger.info("Order placed successfully with ID: {}", placedOrder.getOrderId());
			return ResponseEntity.ok(placedOrder);
		} catch (Exception e) {
			logger.error("Error occurred while placing order: {}", orderDto, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping("/cancel/{orderId}")
	public ResponseEntity<OrderDto> cancelOrder(@PathVariable Integer orderId) {
		logger.info("Received request to cancel order: {}", orderId);

		try {
			OrderDto cancelOrder = orderService.cancelOrder(orderId);
			logger.info("Order deleted successfully with ID: {}", orderId);
			return ResponseEntity.ok(cancelOrder);
		} catch (Exception e) {
			logger.error("Error occurred while deleting order: {}", orderId, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/test-logging")
	public ResponseEntity<String> testLogging() {
		logger.info("Testing logging functionality");

		long startTime = System.currentTimeMillis();

		// Demonstrate different logging levels
		LoggingUtil.demonstrateLoggingLevels();

		// Demonstrate logging with parameters
		LoggingUtil.logWithParameters("test", "123", "success");

		// Demonstrate performance logging
		LoggingUtil.logPerformance("test-logging", startTime);

		return ResponseEntity.ok("Logging test completed. Check console and log files.");
	}

	// New API endpoint to place order using Feign client
	@PostMapping("/feign-place-order")
	public ResponseEntity<OrderDto> placeOrderWithFeign(@RequestBody OrderDto orderDto) {
		logger.info("Received request to place order using Feign client: {}", orderDto);
		try {
			OrderDto result = orderService.placeOrderWithFeign(orderDto);
			logger.info("Order placed successfully using Feign client.");
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			logger.error("Error occurred while placing order using Feign client: {}", orderDto, e);
			return ResponseEntity.internalServerError().build();
		}
	}

	@GetMapping("/feign-getData/{id}")
	public ResponseEntity<UserDto> getUserFromOrderId(@PathVariable Integer id) {
		logger.info("Received request to get User details for ID: {}", id);

		try {
			UserDto dto = orderService.getUserFromOrderId(id);
			logger.info("Successfully retrieved User details for ID: {}", id);
			return ResponseEntity.ok(dto);
		} catch (Exception e) {
			logger.error("Error occurred while retrieving User details for ID: {}", id, e);
			return ResponseEntity.internalServerError().build();
		}
	}

}
