package com.microservice.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemDto {
	private Long id;
	private String skuCode;
	private Double itemSubTotal;
	private Integer quantity;
	private Integer productId;
	private String productName;
	private Double productRate;
}