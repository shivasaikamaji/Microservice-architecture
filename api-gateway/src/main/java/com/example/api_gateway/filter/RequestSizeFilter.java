package com.example.api_gateway.filter;

import java.nio.charset.StandardCharsets;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class RequestSizeFilter implements GlobalFilter, Ordered {

    private static final long MAX_REQUEST_SIZE = 1024 * 1024; // 1 MB

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String contentLength = exchange.getRequest()
                .getHeaders()
                .getFirst("Content-Length");

        if (contentLength != null) {
            try {
                long size = Long.parseLong(contentLength);

                if (size < 0 || size > MAX_REQUEST_SIZE) {
                    return rejectRequest(exchange);
                }
            } catch (NumberFormatException ex) {
                return rejectRequest(exchange);
            }
        }

        return chain.filter(exchange);
    }

    private Mono<Void> rejectRequest(ServerWebExchange exchange) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.PAYLOAD_TOO_LARGE);

        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                  "status": 413,
                  "error": "Payload Too Large",
                  "message": "Request body must not exceed 1 MB"
                }
                """;

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}