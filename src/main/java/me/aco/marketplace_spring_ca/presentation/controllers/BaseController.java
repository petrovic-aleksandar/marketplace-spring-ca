package me.aco.marketplace_spring_ca.presentation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {
    
    protected <T> ResponseEntity<T> created(T body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    protected <T> ResponseEntity<T> noContent(T ignored) {
        return ResponseEntity.noContent().build();
    }

    protected <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }
}
