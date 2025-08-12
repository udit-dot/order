package com.microservice.order.feign;

import com.microservice.order.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "http://localhost:8086", fallback = UserFeignFallback.class)
public interface UserFeignClient {
	@GetMapping("/users/getUser/{id}")
	UserDto getUserById(@PathVariable("id") Integer id);
}
