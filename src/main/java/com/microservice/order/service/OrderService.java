package com.microservice.order.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microservice.order.dto.OrderDto;
import com.microservice.order.dto.UserDto;

@Service
public class OrderService {
	
	@Autowired
	RestTemplate restTemplate;
	
	public OrderDto getDetails(Integer id) {
		
		String url = "http://localhost:8086/users";
		UserDto dtoResponse = restTemplate.getForObject(url, UserDto.class);
		
		System.out.println("User Dto Response : " + dtoResponse.toString());
		return new OrderDto(id, 1500.00, dtoResponse.getUserId(), dtoResponse.getUserName());
	}
	
}
