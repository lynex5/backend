package com.rahul.backend.controller;

import com.rahul.backend.entity.TimelineItem;
import com.rahul.backend.repository.TimelineRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timeline")
public class TimelineController {

    private final TimelineRepository timelineRepo;

    public TimelineController(TimelineRepository timelineRepo) {
        this.timelineRepo = timelineRepo;
    }

    @GetMapping
    public List<TimelineItem> getAllTimelineItems() {
        return timelineRepo.findAllByOrderBySortOrderAsc();
    }

    @PostMapping
    public TimelineItem createTimelineItem(@RequestBody TimelineItem item) {
        return timelineRepo.save(item);
    }

    @PutMapping("/{id}")
    public TimelineItem updateTimelineItem(@PathVariable Long id, @RequestBody TimelineItem item) {
        item.setId(id);
        return timelineRepo.save(item);
    }

    @DeleteMapping("/{id}")
    public void deleteTimelineItem(@PathVariable Long id) {
        timelineRepo.deleteById(id);
    }
}
