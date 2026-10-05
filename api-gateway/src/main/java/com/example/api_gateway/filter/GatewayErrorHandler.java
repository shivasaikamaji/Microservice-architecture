package com.example.api_gateway.filter;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;

import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class GatewayErrorHandler implements WebExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GatewayErrorHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        var response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatusCode status = HttpStatusCode.valueOf(503);
        String message = "Service is temporarily unavailable. Please try again later.";

        if (ex instanceof ResponseStatusException rse
                && rse.getStatusCode().value() == 404) {
            status = rse.getStatusCode();
            message = "No route found for this request.";
        }

        log.error("Gateway error for {} : {}",
                exchange.getRequest().getURI(), ex.getMessage());

        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = "{\"status\":" + status.value()
                + ",\"error\":\"" + message + "\""
                + ",\"path\":\"" + exchange.getRequest().getPath() + "\"}";

        var buffer = response.bufferFactory()
                .wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}