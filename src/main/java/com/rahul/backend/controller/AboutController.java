package com.rahul.backend.controller;

import com.rahul.backend.entity.About;
import com.rahul.backend.repository.AboutRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/about")
public class AboutController {
    private final AboutRepository repo;
    public AboutController(AboutRepository repo) { this.repo = repo; }
    @GetMapping
    public List<About> get() { return repo.findAll(); }
    @PutMapping
    public About update(@RequestBody About about) {
        about.setId(1L); // Force single row
        return repo.save(about);
    }
}
