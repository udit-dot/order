package com.microservice.order.service;

import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microservice.order.dto.OrderDto;
import com.microservice.order.dto.UserDto;
import com.microservice.order.entity.Order;
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
			
			OrderDto orderDto = new OrderDto(id, 1500.00, dtoResponse.getId(), dtoResponse.getName(), LocalDateTime.now());
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
			
			UserDto dtoResponse = restTemplate.getForObject(url, UserDto.class, 1);
			logger.info("Retrieved user data for order: {}", dtoResponse);
			
			Order newOrder = new Order();
			newOrder.setAmount(orderDto.getAmount());
			newOrder.setUserId(dtoResponse.getId());
			newOrder.setUserName(dtoResponse.getName());
			newOrder.setOrderDate(LocalDateTime.now());
			
			logger.debug("Created order entity: {}", newOrder);
			
			Order savedOrder = orderRepository.save(newOrder);
			logger.info("Order saved successfully with ID: {}", savedOrder.getOrderId());
			
			OrderDto result = mapper.map(savedOrder, OrderDto.class);
			logger.debug("Mapped order to DTO: {}", result);
			
			return result;
		} catch (Exception e) {
			logger.error("Error occurred while placing order: {}", orderDto, e);
			throw e;
		}
	}
	
}
