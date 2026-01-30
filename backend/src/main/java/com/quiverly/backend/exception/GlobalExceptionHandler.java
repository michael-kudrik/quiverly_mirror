package com.quiverly.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    //handle validation errors
    @ExceptionHandler
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<com.quiverly.backend.dto.ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex){
        com.quiverly.backend.dto.ErrorResponse error = new com.quiverly.backend.dto.ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<com.quiverly.backend.dto.ErrorResponse> handleDuplicateUsername(DuplicateUsernameException ex){
        com.quiverly.backend.dto.ErrorResponse error = new com.quiverly.backend.dto.ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // handle surfboard not found
    @ExceptionHandler(SurfboardNotFoundException.class)
    public ResponseEntity<com.quiverly.backend.dto.ErrorResponse> handleSurfboardNotFound(SurfboardNotFoundException ex) {
        com.quiverly.backend.dto.ErrorResponse error = new com.quiverly.backend.dto.ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
