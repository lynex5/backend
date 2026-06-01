package com.rahul.backend.service;

import com.rahul.backend.entity.GfgConfig;
import com.rahul.backend.repository.GfgConfigRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class GfgConfigService {

    private final GfgConfigRepository repository;

    public GfgConfigService(GfgConfigRepository repository) {
        this.repository = repository;
    }

    public GfgConfig getConfig() {
        return repository.findById(1L).orElse(null);
    }

    public GfgConfig updateConfig(String fullCookieString) {
        GfgConfig config = repository.findById(1L).orElse(new GfgConfig());
        config.setId(1L);
        config.setFullCookieString(fullCookieString);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        config.setUpdatedAt(LocalDateTime.now().format(formatter));
        
        return repository.save(config);
    }
}
