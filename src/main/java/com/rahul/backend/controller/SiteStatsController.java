package com.rahul.backend.controller;

import com.rahul.backend.entity.SiteStats;
import com.rahul.backend.repository.SiteStatsRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats")
public class SiteStatsController {

    private final SiteStatsRepository statsRepo;

    public SiteStatsController(SiteStatsRepository statsRepo) {
        this.statsRepo = statsRepo;
    }

    @GetMapping
    public SiteStats getStats() {
        return statsRepo.findById(1L).orElse(null);
    }

    @PutMapping
    public SiteStats updateStats(@RequestBody SiteStats stats) {
        stats.setId(1L); // Force ID to 1
        return statsRepo.save(stats);
    }
}
