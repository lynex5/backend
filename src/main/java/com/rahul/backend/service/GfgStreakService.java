package com.rahul.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.backend.entity.GfgConfig;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GfgStreakService {

    private final GfgConfigService configService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Map<String, Object> cachedStreakData = null;

    public GfgStreakService(GfgConfigService configService) {
        this.configService = configService;
    }

    public Map<String, Object> getStreakData() {
        if (cachedStreakData != null) {
            return cachedStreakData;
        }
        return fetchAndCacheStreakData();
    }

    @Scheduled(fixedRate = 3600000) // 1 hour
    public void scheduledRefresh() {
        fetchAndCacheStreakData();
    }

    public Map<String, Object> fetchAndCacheStreakData() {
        GfgConfig config = configService.getConfig();
        if (config == null || config.getFullCookieString() == null || config.getFullCookieString().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "cookies not configured");
            return error;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Cookie", config.getFullCookieString());
            headers.set("Referer", "https://www.geeksforgeeks.org/");
            headers.set("Origin", "https://www.geeksforgeeks.org");
            headers.set("Accept", "*/*");
            headers.set("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8");
            headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Safari/537.36");
            headers.set("sec-fetch-site", "same-site");
            headers.set("sec-fetch-mode", "cors");
            headers.set("sec-fetch-dest", "empty");
            headers.set("priority", "u=1, i");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://practiceapi.geeksforgeeks.org/api/latest/problems-of-day/my-pod-profile/",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            
            Map<String, Object> result = new HashMap<>();
            
            int currentStreak = root.path("current_streak").asInt(root.path("currentStreak").asInt(root.path("streak").asInt(root.path("pod_streak").asInt(0))));
            int longestStreak = root.path("longest_streak").asInt(root.path("longestStreak").asInt(root.path("max_streak").asInt(root.path("max_pod_streak").asInt(0))));
            int correctSubmissions = root.path("correct_submissions").asInt(root.path("correctSubmissions").asInt(root.path("total_solved").asInt(root.path("total_pod_solved").asInt(0))));
            int attemptedProblems = root.path("attempted_problems").asInt(root.path("attemptedProblems").asInt(root.path("total_attempted").asInt(root.path("total_pod_attempted").asInt(0))));
            String lastCorrect = root.path("last_correct_submission").asText(root.path("lastCorrectSubmission").asText(root.path("last_solved").asText(root.path("last_pod_solved").asText(""))));

            result.put("currentStreak", currentStreak);
            result.put("longestStreak", longestStreak);
            result.put("correctSubmissions", correctSubmissions);
            result.put("attemptedProblems", attemptedProblems);
            result.put("lastCorrectSubmission", lastCorrect);

            cachedStreakData = result;
            return result;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "failed to fetch streak data");
            error.put("details", e.getMessage());
            return error;
        }
    }
}
