package com.microservice.order.feign;

import com.microservice.order.dto.UserDto;
import com.microservice.order.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name = "user-service", url = "http://localhost:8086")
//public interface UserFeignClient {
//  @GetMapping("/users/getUser/{id}")
//  UserDto getUserById(@PathVariable("id") Integer id);
//}
//
//@FeignClient(name = "product-service", url = "http://localhost:8087")
//public interface ProductFeignClient {
//  @GetMapping("/products/{id}")
//  ProductDto getProductById(@PathVariable("id") Integer id);
//
//  @PutMapping("/inventory/{inventoryId}/quantity")
//  void updateInventoryQuantity(@PathVariable("inventoryId") Integer inventoryId,
//      @RequestParam("quantity") Integer quantity);
//}
