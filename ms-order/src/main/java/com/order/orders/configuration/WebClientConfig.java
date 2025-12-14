package com.order.orders.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${service.product.url}")
    private String urlMsProducts;
    public static final String MS_PRODUCT_BASE_URL = "/api/v1/products";

    @Value("${service.user.url}")
    private String urlMsUsers;
    public static final String MS_USER_BASE_URL = "/api/v1/users";

    @Bean
    public WebClient productServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(urlMsProducts)
                .build();
    }

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(urlMsUsers)
                .build();
    }
}