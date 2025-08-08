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
import com.microservice.order.repo.OrderRepository;

@Service
public class OrderService {
	
	private static final Logger logger = LogManager.getLogger(OrderService.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	OrderRepository orderRepository;
	
	@Autowired
	ModelMapper mapper;
	
	public OrderDto getDetails(Integer id) {
		logger.debug("Getting order details for ID: {}", id);
		
		try {
			String url = "http://localhost:8086/users";
			logger.debug("Calling user service at URL: {}", url);
			
			UserDto dtoResponse = restTemplate.getForObject(url, UserDto.class);
			logger.info("Successfully retrieved user data: {}", dtoResponse);
			
			OrderDto orderDto = new OrderDto(id, 1500.00, dtoResponse.getId(), dtoResponse.getName(), LocalDateTime.now(), null);
			logger.debug("Created order DTO: {}", orderDto);
			
			return orderDto;
		} catch (Exception e) {
			logger.error("Error occurred while getting order details for ID: {}", id, e);
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
					ProductDto productDto = restTemplate.getForObject(productUrl, ProductDto.class, lineItemDto.getProductId());

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
	
}
