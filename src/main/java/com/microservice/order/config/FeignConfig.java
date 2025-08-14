package com.microservice.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Request;

@Configuration
public class FeignConfig {

    @Bean
    public Request.Options feignRequestOptions() {
        // connectTimeout, readTimeout, followRedirects
        return new Request.Options(5000,5000);
    }
}
