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
