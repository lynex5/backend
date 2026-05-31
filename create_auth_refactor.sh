#!/bin/bash
mkdir -p src/main/java/com/rahul/backend/service
mkdir -p src/main/java/com/rahul/backend/dto
mkdir -p src/main/java/com/rahul/backend/exception
mkdir -p src/main/java/com/rahul/backend/filter
mkdir -p src/main/java/com/rahul/backend/config
mkdir -p src/main/java/com/rahul/backend/util

# 1. Properties
cat << 'PROP' >> src/main/resources/application.properties

# Application Properties
jwt.secret=your-256-bit-minimum-secret-key-here-must-be-very-long-and-secure
jwt.expiration=86400000
auth.max.attempts=5
auth.lock.duration.minutes=15
spring.security.bcrypt.strength=12
server.error.include-stacktrace=never
server.error.include-message=never
server.error.include-binding-errors=never
PROP

# 2. DTO
cat << 'DTO' > src/main/java/com/rahul/backend/dto/LoginRequest.java
package com.rahul.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Size(max = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100)
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
DTO

# 3. Exceptions
cat << 'EX' > src/main/java/com/rahul/backend/exception/BadCredentialsException.java
package com.rahul.backend.exception;
public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) { super(message); }
}
EX

cat << 'EX' > src/main/java/com/rahul/backend/exception/AccountLockedException.java
package com.rahul.backend.exception;
public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) { super(message); }
}
EX

cat << 'EX' > src/main/java/com/rahul/backend/exception/InvalidInputException.java
package com.rahul.backend.exception;
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) { super(message); }
}
EX

cat << 'EX' > src/main/java/com/rahul/backend/exception/GlobalExceptionHandler.java
package com.rahul.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "ACCESS_DENIED", "Invalid credentials");
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<?> handleAccountLocked(AccountLockedException ex) {
        return buildResponse(HttpStatus.LOCKED, "ACCOUNT_LOCKED", ex.getMessage());
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<?> handleInvalidInput(InvalidInputException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_INPUT", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", error);
        body.put("message", message);
        body.put("timestamp", Instant.now());
        return new ResponseEntity<>(body, status);
    }
}
EX

