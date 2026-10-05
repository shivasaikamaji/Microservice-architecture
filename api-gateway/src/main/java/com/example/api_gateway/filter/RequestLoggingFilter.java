package com.example.api_gateway.filter;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(RequestLoggingFilter.class);

    private static final String HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        long start = System.currentTimeMillis();

        // Step 7: use the incoming ID if present, otherwise create one
        String incoming = exchange.getRequest().getHeaders().getFirst(HEADER);
        final String requestId =
                (incoming == null || incoming.isBlank())
                        ? UUID.randomUUID().toString()
                        : incoming;

        String method = exchange.getRequest().getMethod().name();
        String url = exchange.getRequest().getURI().toString();

        // Step 8: add the ID to the request sent to the service
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(HEADER, requestId)
                .build();
        ServerWebExchange newExchange =
                exchange.mutate().request(request).build();

        // also return it to the client so you can see it in Postman
        newExchange.getResponse().getHeaders().add(HEADER, requestId);

        return chain.filter(newExchange).then(Mono.fromRunnable(() -> {
            Route route = newExchange.getAttribute(
                    ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            String service = (route != null) ? route.getId() : "unknown";
            long time = System.currentTimeMillis() - start;

            log.info("RequestID={} | Method={} | URL={} | Service={} | Status={} | Time={}ms",
                    requestId, method, url, service,
                    newExchange.getResponse().getStatusCode(), time);
        }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}