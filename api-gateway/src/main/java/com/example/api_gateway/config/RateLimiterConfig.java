package com.example.api_gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    // Decides WHO is being counted: the X-User-Id header, or the IP address
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String user = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (user != null && !user.isBlank()) {
                return Mono.just(user);
            }
            var addr = exchange.getRequest().getRemoteAddress();
            String ip = (addr != null) ? addr.getAddress().getHostAddress() : "unknown";
            return Mono.just(ip);
        };
    }
}