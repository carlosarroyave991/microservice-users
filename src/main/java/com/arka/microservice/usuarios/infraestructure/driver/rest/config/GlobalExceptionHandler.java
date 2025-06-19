package com.arka.microservice.usuarios.infraestructure.driver.rest.config;

import com.arka.microservice.usuarios.domain.exception.BusinessException;
import com.arka.microservice.usuarios.domain.exception.error.CommonErrorCode;
import com.arka.microservice.usuarios.domain.exception.error.ErrorCode;
import com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.resp.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@Order(-2)
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Exception caught in global error handler: {}", ex.getMessage(), ex);

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ErrorResponse errorResponse;

        if (ex instanceof BusinessException businessException) {
            // custom business exceptions
            exchange.getResponse().setStatusCode(HttpStatus.valueOf(businessException.getStatusCode()));
            errorResponse = new ErrorResponse(businessException.getCode(), businessException.getMessage());
        } else if (ex instanceof ResponseStatusException responseStatusException) {
            // Spring's ResponseStatusException
            exchange.getResponse().setStatusCode(responseStatusException.getStatusCode());

            String code = "ERR_" + responseStatusException.getStatusCode().value();
            String message = responseStatusException.getReason() != null ?
                    responseStatusException.getReason() : responseStatusException.getMessage();

            errorResponse = new ErrorResponse(code, message);
        } else {
            // Handle all other exceptions
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);

            // Use the internal server error code from our registry
            ErrorCode errorCode = CommonErrorCode.INTERNAL_ERROR;
            errorResponse = new ErrorResponse(
                    errorCode.getCode(),
                    "An unexpected error occurred: " + ex.getMessage()
            );
        }

        DataBuffer dataBuffer;
        try {
            dataBuffer = bufferFactory.wrap(objectMapper.writeValueAsBytes(errorResponse));
        } catch (JsonProcessingException e) {
            log.error("Error writing error response as JSON", e);
            dataBuffer = bufferFactory.wrap("Error processing response".getBytes());
        }

        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }
}