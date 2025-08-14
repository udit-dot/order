package com.microservice.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.microservice.order.config.FeignConfig;
import com.microservice.order.dto.UserDto;

@FeignClient(name = "user-service", url = "http://localhost:8086", fallback = UserFeignFallback.class,  configuration = FeignConfig.class)
public interface UserFeignClient {
	@GetMapping("/users/getUser/{id}")
	UserDto getUserById(@PathVariable("id") Integer id);
}
