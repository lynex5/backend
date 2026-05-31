#!/bin/bash
mkdir -p src/main/java/com/rahul/backend/controller

cat << 'PROJ' > src/main/java/com/rahul/backend/controller/ProjectController.java
package com.rahul.backend.controller;

import com.rahul.backend.entity.Project;
import com.rahul.backend.repository.ProjectRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectRepository repo;

    public ProjectController(ProjectRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Project> getAll() { return repo.findAll(); }

    @PostMapping
    public Project create(@RequestBody Project project) { return repo.save(project); }

    @PutMapping("/{id}")
    public Project update(@PathVariable Long id, @RequestBody Project project) {
        project.setId(id);
        return repo.save(project);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}
PROJ

cat << 'SKILL' > src/main/java/com/rahul/backend/controller/SkillController.java
package com.rahul.backend.controller;

import com.rahul.backend.entity.Skill;
import com.rahul.backend.repository.SkillRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
public class SkillController {
    private final SkillRepository repo;
    public SkillController(SkillRepository repo) { this.repo = repo; }
    @GetMapping
    public List<Skill> getAll() { return repo.findAll(); }
    @PostMapping
    public Skill create(@RequestBody Skill skill) { return repo.save(skill); }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { repo.deleteById(id); }
}
SKILL

cat << 'ABT' > src/main/java/com/rahul/backend/controller/AboutController.java
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
ABT

cat << 'CON' > src/main/java/com/rahul/backend/controller/ContactController.java
package com.rahul.backend.controller;

import com.rahul.backend.entity.Contact;
import com.rahul.backend.repository.ContactRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactController {
    private final ContactRepository repo;
    public ContactController(ContactRepository repo) { this.repo = repo; }
    @GetMapping
    public List<Contact> get() { return repo.findAll(); }
    @PutMapping
    public Contact update(@RequestBody Contact contact) {
        contact.setId(1L);
        return repo.save(contact);
    }
}
CON

cat << 'RES' > src/main/java/com/rahul/backend/controller/ResumeController.java
package com.rahul.backend.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public ResumeController() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve("resume.pdf");
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.ok(Map.of("message", "Resume uploaded successfully"));
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Could not store file"));
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile() {
        try {
            Path filePath = this.fileStorageLocation.resolve("resume.pdf").normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/pdf"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteFile() {
        try {
            Path filePath = this.fileStorageLocation.resolve("resume.pdf").normalize();
            Files.deleteIfExists(filePath);
            return ResponseEntity.ok(Map.of("message", "Resume deleted successfully"));
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Could not delete file"));
        }
    }
}
RES
