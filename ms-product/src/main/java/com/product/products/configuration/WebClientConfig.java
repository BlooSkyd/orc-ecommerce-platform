package com.product.products.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${service.order.url}")
    private String urlMsOrders;
    public static final String MS_ORDER_BASE_URL = "/api/v1/orders";


    @Bean
    public WebClient orderServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(urlMsOrders)
                .build();
    }

}