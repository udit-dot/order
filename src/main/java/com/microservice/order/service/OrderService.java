package com.microservice.order.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microservice.order.dto.OrderDto;
import com.microservice.order.dto.OrderLineItemDto;
import com.microservice.order.dto.ProductDto;
import com.microservice.order.dto.UserDto;
import com.microservice.order.entity.Order;
import com.microservice.order.entity.OrderLineItem;
import com.microservice.order.feign.ProductFeignClient;
import com.microservice.order.feign.UserFeignClient;
import com.microservice.order.repo.OrderRepository;

import io.github.resilience4j.timelimiter.TimeLimiterRegistry;

@Service
public class OrderService {

	private static final Logger logger = LogManager.getLogger(OrderService.class);

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	ModelMapper mapper;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	UserFeignClient userFeignClient;

	@Autowired
	ProductFeignClient productFeignClient;

	@Autowired
	private TimeLimiterRegistry timeLimiterRegistry;

	// Example method using Feign client for user and product service
	public OrderDto placeOrderWithFeign(OrderDto orderDto) {
		logger.info("Placing order with Feign: {}", orderDto);
		try {
			// Get user details using Feign
			UserDto dtoResponse = userFeignClient.getUserById(orderDto.getUserId());
			logger.info("Retrieved user data for order (Feign): {}", dtoResponse);
			if ("Unknown User (Fallback)".equals(dtoResponse.getName())) {
				logger.warn("User service unavailable, using fallback user for id: {}", orderDto.getUserId());
			}
			Order order = mapper.map(orderDto, Order.class);
			order.setUserId(dtoResponse.getId());
			order.setUserName(dtoResponse.getName());
			order.setOrderDate(LocalDateTime.now());
			List<OrderLineItem> orderLineItemsList = new ArrayList<>();
			Double totalAmount = 0.0;
			if (orderDto.getOrderLineItems() != null) {
				for (OrderLineItemDto lineItemDto : orderDto.getOrderLineItems()) {
					ProductDto productDto = productFeignClient.getProductById(lineItemDto.getProductId());
					if (productDto == null) {
						throw new RuntimeException("Product not found with id: " + lineItemDto.getProductId());
					}
					if (productDto.getInventory() == null) {
						throw new RuntimeException("Inventory not found for product id: " + lineItemDto.getProductId());
					}
					if ("OUT_OF_STOCK".equals(productDto.getInventory().getStatus())) {
						throw new RuntimeException("Product " + productDto.getProductName()
								+ " is not available for ordering. Status: " + productDto.getInventory().getStatus());
					}
					if (productDto.getInventory().getQuantity() < lineItemDto.getQuantity()) {
						throw new RuntimeException("Insufficient quantity for product " + productDto.getProductName()
								+ ". Available: " + productDto.getInventory().getQuantity() + ", Requested: "
								+ lineItemDto.getQuantity());
					}
					Integer newQuantity = productDto.getInventory().getQuantity() - lineItemDto.getQuantity();
					productFeignClient.updateInventoryQuantity(productDto.getInventory().getInventoryId(), newQuantity);
					OrderLineItem orderLineItem = mapper.map(lineItemDto, OrderLineItem.class);
					orderLineItem.setProductId(productDto.getProductId());
					orderLineItem.setProductName(productDto.getProductName());
					orderLineItem.setProductRate(productDto.getPrice());
					orderLineItem.setItemSubTotal(productDto.getPrice() * lineItemDto.getQuantity());
					orderLineItem.setSkuCode("SKU-" + UUID.randomUUID().toString() + "-" + productDto.getProductId());
					totalAmount += orderLineItem.getItemSubTotal();
					orderLineItemsList.add(orderLineItem);
				}
			}
			order.setAmount(totalAmount);
			order.setOrderLineItems(orderLineItemsList);
			order.setStatus("PLACED");
			Order savedOrder = orderRepository.save(order);
			logger.info("Order saved successfully with ID (Feign): {}", savedOrder.getOrderId());
			OrderDto result = mapper.map(savedOrder, OrderDto.class);
			logger.debug("Mapped order to DTO (Feign): {}", result);
			return result;
		} catch (Exception e) {
			logger.error("Error occurred while placing order with Feign: {}", orderDto, e);
			throw e;
		}
	}

	public UserDto getUserFromOrderId(Integer orderId) throws Exception {
		logger.debug("Getting User details for ID: {}", orderId);

		try {
			Order order = orderRepository.findById(orderId)
					.orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

			// Rest Template call to user service
//			String url = "http://localhost:8086/users/getUser/{id}";
//			UserDto userDto = restTemplate.getForObject(url, UserDto.class, order.getUserId());

			// Feign Client call to user service
			UserDto userDto = userFeignClient.getUserById(order.getUserId());
			if ("Unknown User (Fallback)".equals(userDto.getName())) {
				logger.warn("User service unavailable, using fallback user for id: {}", order.getUserId());
			}
			return userDto;
		} catch (Exception e) {
			logger.error("Error occurred while getting order details for ID: {} , {}", orderId, e.getMessage());
			throw e;
		}
	}

	public OrderDto getDetails(Integer orderId) {
		logger.debug("Getting order details for ID: {}", orderId);

		try {
			Order order = orderRepository.findById(orderId)
					.orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
			return mapper.map(order, OrderDto.class);
		} catch (Exception e) {
			logger.error("Error occurred while getting order details for ID: {}", orderId, e);
			throw e;
		}
	}

	public OrderDto placeOrder(OrderDto orderDto) {
		logger.info("Placing order: {}", orderDto);

		try {
			String url = "http://localhost:8086/users/getUser/{id}";
			logger.debug("Calling user service to get user details at URL: {}", url);

			UserDto dtoResponse = restTemplate.getForObject(url, UserDto.class, orderDto.getUserId());
			logger.info("Retrieved user data for order: {}", dtoResponse);

			if (dtoResponse == null) {
				throw new RuntimeException("User not found with id: " + orderDto.getUserId());
			}

			Order order = mapper.map(orderDto, Order.class);
			order.setUserId(dtoResponse.getId());
			order.setUserName(dtoResponse.getName());
			order.setOrderDate(LocalDateTime.now());

			// Process order line items and fetch product details with inventory validation
			List<OrderLineItem> orderLineItemsList = new ArrayList<>();
			Double totalAmount = 0.0;
			if (orderDto.getOrderLineItems() != null) {
				for (OrderLineItemDto lineItemDto : orderDto.getOrderLineItems()) {
					// Fetch product details with inventory check from Product Service
					String productUrl = "http://localhost:8087/products/{id}";
					ProductDto productDto = restTemplate.getForObject(productUrl, ProductDto.class,
							lineItemDto.getProductId());

					if (productDto == null) {
						throw new RuntimeException("Product not found with id: " + lineItemDto.getProductId());
					}

					// Check inventory availability
					if (productDto.getInventory() == null) {
						throw new RuntimeException("Inventory not found for product id: " + lineItemDto.getProductId());
					}

					// Validate inventory status and quantity
					if ("OUT_OF_STOCK".equals(productDto.getInventory().getStatus())) {
						throw new RuntimeException("Product " + productDto.getProductName()
								+ " is not available for ordering. Status: " + productDto.getInventory().getStatus());
					}

					if (productDto.getInventory().getQuantity() < lineItemDto.getQuantity()) {
						throw new RuntimeException("Insufficient quantity for product " + productDto.getProductName()
								+ ". Available: " + productDto.getInventory().getQuantity() + ", Requested: "
								+ lineItemDto.getQuantity());
					}

					// Update inventory quantity in Product Service
					Integer newQuantity = productDto.getInventory().getQuantity() - lineItemDto.getQuantity();
					String inventoryUpdateUrl = "http://localhost:8087/inventory/"
							+ productDto.getInventory().getInventoryId() + "/quantity?quantity=" + newQuantity;
					restTemplate.put(inventoryUpdateUrl, null);

					// Create OrderLineItems entity using ModelMapper
					OrderLineItem orderLineItem = mapper.map(lineItemDto, OrderLineItem.class);
					orderLineItem.setProductId(productDto.getProductId());
					orderLineItem.setProductName(productDto.getProductName());
					orderLineItem.setProductRate(productDto.getPrice());
					orderLineItem.setItemSubTotal(productDto.getPrice() * lineItemDto.getQuantity());
					orderLineItem.setSkuCode("SKU-" + UUID.randomUUID().toString() + "-" + productDto.getProductId());

					totalAmount += orderLineItem.getItemSubTotal();
					orderLineItemsList.add(orderLineItem);
				}
			}
			order.setAmount(totalAmount);
			order.setOrderLineItems(orderLineItemsList);
			order.setStatus("PLACED");
			// Save the order to database
			Order savedOrder = orderRepository.save(order);
			logger.info("Order saved successfully with ID: {}", savedOrder.getOrderId());
			// Convert back to DTO for response using ModelMapper
			OrderDto result = mapper.map(savedOrder, OrderDto.class);
			logger.debug("Mapped order to DTO: {}", result);

			return result;
		} catch (Exception e) {
			logger.error("Error occurred while placing order: {}", orderDto, e);
			throw e;
		}
	}

	// Cancel order using RestTemplate (exchange/postForEntity)
	public OrderDto cancelOrderWithRestTemplate(Integer orderId) {
		logger.info("Cancelling order with ID (RestTemplate): {}", orderId);
		try {
			Order order = orderRepository.findById(orderId)
					.orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

			// Restore inventory for each product in the order
			if (order.getOrderLineItems() != null) {
				for (OrderLineItem lineItem : order.getOrderLineItems()) {
					String productUrl = "http://localhost:8087/products/{id}";
					ProductDto productDto = restTemplate.getForObject(productUrl, ProductDto.class,
							lineItem.getProductId());
					if (productDto == null || productDto.getInventory() == null) {
						throw new RuntimeException("Product or inventory not found for ID: " + lineItem.getProductId());
					}
					Integer restoredQuantity = productDto.getInventory().getQuantity() + lineItem.getQuantity();
					String inventoryUpdateUrl = "http://localhost:8087/inventory/"
							+ productDto.getInventory().getInventoryId() + "/quantity?quantity=" + restoredQuantity;
					org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
					headers.set("content-type", "application/json");
					org.springframework.http.HttpEntity<Void> request = new org.springframework.http.HttpEntity<>(
							headers);
					restTemplate.exchange(inventoryUpdateUrl, org.springframework.http.HttpMethod.PUT, request,
							Void.class);
				}
			}
			order.setStatus("CANCELLED");
			Order cancelledOrder = orderRepository.save(order);
			logger.info("Order with ID {} has been cancelled successfully (RestTemplate).", orderId);
			return mapper.map(cancelledOrder, OrderDto.class);
		} catch (Exception e) {
			logger.error("Error occurred while cancelling order ID (RestTemplate): {}", orderId, e);
			throw e;
		}
	}

	// Cancel order using Feign client
	public OrderDto cancelOrderWithFeign(Integer orderId) {
		logger.info("Cancelling order with ID (Feign): {}", orderId);
		try {
			Order order = orderRepository.findById(orderId)
					.orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

			// Restore inventory for each product in the order
			if (order.getOrderLineItems() != null) {
				for (OrderLineItem lineItem : order.getOrderLineItems()) {
					ProductDto productDto = productFeignClient.getProductById(lineItem.getProductId());
					if (productDto == null || productDto.getInventory() == null) {
						throw new RuntimeException("Product or inventory not found for ID: " + lineItem.getProductId());
					}
					Integer restoredQuantity = productDto.getInventory().getQuantity() + lineItem.getQuantity();
					productFeignClient.updateInventoryQuantity(productDto.getInventory().getInventoryId(),
							restoredQuantity);
				}
			}

			order.setStatus("CANCELLED");
			Order cancelledOrder = orderRepository.save(order);
			logger.info("Order with ID {} has been cancelled successfully (Feign).", orderId);
			return mapper.map(cancelledOrder, OrderDto.class);
		} catch (Exception e) {
			logger.error("Error occurred while cancelling order ID (Feign): {}", orderId, e);
			throw e;
		}
	}

}
