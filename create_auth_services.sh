#!/bin/bash

# 4. JwtUtil Validation & Security
cat << 'UTIL' > src/main/java/com/rahul/backend/config/JwtUtil.java
// Replacing the old JwtUtil if it wasn't in config
UTIL

cat << 'UTIL' > src/main/java/com/rahul/backend/util/JwtUtil.java
package com.rahul.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}
UTIL

# 5. Login Attempt Service
cat << 'SVC' > src/main/java/com/rahul/backend/service/LoginAttemptService.java
package com.rahul.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    @Value("${auth.max.attempts}")
    private int maxAttempts;

    @Value("${auth.lock.duration.minutes}")
    private long lockDurationMinutes;

    private static class AttemptRecord {
        int count;
        LocalDateTime firstAttemptTime;

        AttemptRecord() {
            this.count = 1;
            this.firstAttemptTime = LocalDateTime.now();
        }
    }

    private final ConcurrentHashMap<String, AttemptRecord> memory = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        memory.remove(username);
    }

    public void loginFailed(String username) {
        memory.compute(username, (key, record) -> {
            if (record == null || isExpired(record.firstAttemptTime)) {
                return new AttemptRecord();
            }
            record.count++;
            return record;
        });
    }

    public boolean isBlocked(String username) {
        AttemptRecord record = memory.get(username);
        if (record == null) return false;
        if (isExpired(record.firstAttemptTime)) {
            memory.remove(username);
            return false;
        }
        return record.count >= maxAttempts;
    }

    private boolean isExpired(LocalDateTime time) {
        return ChronoUnit.MINUTES.between(time, LocalDateTime.now()) > lockDurationMinutes;
    }
}
SVC

# 6. Auth Service
cat << 'SVC2' > src/main/java/com/rahul/backend/service/AuthService.java
package com.rahul.backend.service;

import com.rahul.backend.entity.AdminUser;
import com.rahul.backend.exception.AccountLockedException;
import com.rahul.backend.exception.BadCredentialsException;
import com.rahul.backend.exception.InvalidInputException;
import com.rahul.backend.repository.AdminUserRepository;
import com.rahul.backend.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AdminUserRepository adminRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    public AuthService(AdminUserRepository adminRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, LoginAttemptService loginAttemptService) {
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
    }

    public String login(String username, String password) {
        // 1. Sanitize
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new InvalidInputException("Credentials cannot be empty");
        }
        String sanitizedUsername = username.trim();

        // 2. Rate limit check
        if (loginAttemptService.isBlocked(sanitizedUsername)) {
            throw new AccountLockedException("Too many invalid attempts. Try again later.");
        }

        // 3. Fetch user
        AdminUser user = adminRepo.findByUsername(sanitizedUsername).orElse(null);

        // Dummy check to prevent timing attacks if user doesn't exist
        if (user == null) {
            passwordEncoder.matches(password, passwordEncoder.encode("dummy"));
            loginAttemptService.loginFailed(sanitizedUsername);
            throw new BadCredentialsException("Invalid credentials");
        }

        // 4. Verification
        if (!passwordEncoder.matches(password, user.getPassword())) {
            loginAttemptService.loginFailed(sanitizedUsername);
            throw new BadCredentialsException("Invalid credentials");
        }

        // 5. Success
        loginAttemptService.loginSucceeded(sanitizedUsername);
        
        // MFA HOOK — Step 2 will go here
        // if (user.isMfaEnabled()) {
        //     return intermediate token (not full JWT)
        //     frontend redirects to /verify-totp
        // }
        // TOTP verification service will be added in next iteration

        return jwtUtil.generateToken(sanitizedUsername);
    }
}
SVC2
