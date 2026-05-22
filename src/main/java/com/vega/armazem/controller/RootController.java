package com.vega.armazem.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
                "application", "Armazém de Ingredientes API",
                "status", "UP",
                "health", "/health",
                "swagger", "/swagger-ui.html",
                "openApi", "/v3/api-docs"
        ));
    }
}