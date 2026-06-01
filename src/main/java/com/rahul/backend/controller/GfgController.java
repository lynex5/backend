package com.rahul.backend.controller;

import com.rahul.backend.entity.GfgConfig;
import com.rahul.backend.service.GfgConfigService;
import com.rahul.backend.service.GfgStreakService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class GfgController {

    private final GfgConfigService configService;
    private final GfgStreakService streakService;

    public GfgController(GfgConfigService configService, GfgStreakService streakService) {
        this.configService = configService;
        this.streakService = streakService;
    }

    @GetMapping("/gfg/streak")
    public Map<String, Object> getStreak() {
        return streakService.getStreakData();
    }

    @GetMapping("/admin/gfg-cookies/status")
    public Map<String, Object> getCookieStatus() {
        GfgConfig config = configService.getConfig();
        Map<String, Object> response = new HashMap<>();
        if (config != null && config.getFullCookieString() != null && !config.getFullCookieString().isEmpty()) {
            response.put("status", "ACTIVE");
            response.put("updatedAt", config.getUpdatedAt());
        } else {
            response.put("status", "NOT_SET");
        }
        return response;
    }

    @PutMapping("/admin/gfg-cookies")
    public Map<String, Object> updateCookies(@RequestBody Map<String, String> request) {
        String fullCookieString = request.get("fullCookieString");
        GfgConfig config = configService.updateConfig(fullCookieString);
        
        // Force refresh the streak data when cookies are updated
        streakService.fetchAndCacheStreakData();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "updated");
        response.put("updatedAt", config.getUpdatedAt());
        return response;
    }
}
