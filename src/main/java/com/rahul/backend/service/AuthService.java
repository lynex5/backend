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
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
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
