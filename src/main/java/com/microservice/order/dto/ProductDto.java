package com.microservice.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

	private Integer productId;
	private String productName;
	private String description;
	private Double price;
	private String category;
	private InventoryDto inventory;
}
