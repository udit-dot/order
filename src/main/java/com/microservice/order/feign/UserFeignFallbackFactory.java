package com.microservice.order.feign;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import com.microservice.order.dto.UserDto;

@Component
public class UserFeignFallbackFactory implements FallbackFactory<UserFeignClient> {
    @Override
    public UserFeignClient create(Throwable cause) {
        return new UserFeignClient() {
            @Override
            public UserDto getUserById(Integer id) {
                UserDto fallbackUser = new UserDto();
                fallbackUser.setId(id);
                fallbackUser.setName("Unknown User (Fallback)");
                fallbackUser.setAddress("Unknown address (Fallback)");
                fallbackUser.setPhone("Unknown number (Fallback)");
                return fallbackUser;
            }
        };
    }
}
