package com.arka.microservice.usuarios.infraestructure.driver.rest.controller;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/diagnostic")
public class DiagnosticController {

    private final ConnectionFactory connectionFactory;

    @Autowired
    public DiagnosticController(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @GetMapping("/db-status")
    public Mono<ResponseEntity<Map<String, Object>>> checkDatabaseStatus() {
        DatabaseClient client = DatabaseClient.create(connectionFactory);
        
        return client.sql("SELECT 1 as result")
                .map(row -> row.get("result", Integer.class))
                .one()
                .map(result -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "UP");
                    response.put("database", "PostgreSQL");
                    response.put("result", result);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "DOWN");
                    response.put("error", e.getMessage());
                    return Mono.just(ResponseEntity.status(500).body(response));
                });
    }
}