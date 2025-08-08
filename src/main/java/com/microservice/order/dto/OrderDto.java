package com.microservice.order.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

	private Integer orderId;
	private Double amount;
	private Integer userId;
	private String userName;
	private LocalDateTime orderDate;
	private List<OrderLineItemDto> orderLineItems;
}
