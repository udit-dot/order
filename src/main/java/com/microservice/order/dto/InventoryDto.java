package com.microservice.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto {

	private Integer inventoryId;
	private Integer quantity;
	private String location;
	private String status;
	private Integer productId;
}
