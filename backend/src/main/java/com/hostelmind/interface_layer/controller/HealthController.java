package com.hostelmind.interface_layer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping({"/api/v1/health", "/api/v1", "/health"})
public class HealthController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "app", "HostelMind-AI",
                "timestamp", Instant.now().toString(),
                "environment", "production-ready"
        ));
    }
}
