package com.microservice.order.feign;

import com.microservice.order.dto.ProductDto;
import org.springframework.stereotype.Component;

@Component
public class ProductFeignFallback implements ProductFeignClient {
  @Override
  public ProductDto getProductById(Integer id) {
    ProductDto fallbackProduct = new ProductDto();
    fallbackProduct.setProductId(id);
    fallbackProduct.setProductName("Unknown Product (Fallback)");
    // Set other default fields if needed
    return fallbackProduct;
  }

  @Override
  public void updateInventoryQuantity(Integer inventoryId, Integer quantity) {
    // Fallback: do nothing or log
  }
}
