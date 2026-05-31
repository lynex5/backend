package com.rahul.backend.repository;

import com.rahul.backend.entity.SiteStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteStatsRepository extends JpaRepository<SiteStats, Long> {
}
