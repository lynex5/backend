package com.rahul.backend.repository;

import com.rahul.backend.entity.TimelineItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimelineRepository extends JpaRepository<TimelineItem, Long> {
    List<TimelineItem> findAllByOrderBySortOrderAsc();
}
