package com.rahul.backend.repository;

import com.rahul.backend.entity.GfgConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GfgConfigRepository extends JpaRepository<GfgConfig, Long> {
}
