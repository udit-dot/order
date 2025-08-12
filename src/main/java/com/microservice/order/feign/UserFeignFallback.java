package com.microservice.order.feign;

import com.microservice.order.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserFeignFallback implements UserFeignClient {
  @Override
  public UserDto getUserById(Integer id) {
    UserDto fallbackUser = new UserDto();
    fallbackUser.setId(id);
    fallbackUser.setName("Unknown User (Fallback)");
    fallbackUser.setAddress("Unknown address (Fallback)");
    fallbackUser.setPhone("Unknown number (Fallback)");
    return fallbackUser;
  }
}
