package com.arka.microservice.usuarios.infraestructure.driver.rest.dto.user.resp;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private String timestamp;
    private Map<String, Object> details;
    
    // Getters necesarios para la serialización JSON
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public Map<String, Object> getDetails() {
        return details;
    }

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public ErrorResponse(String code, String message, Map<String, Object> details) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
    }
}
