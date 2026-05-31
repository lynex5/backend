package com.rahul.backend.controller;

import com.rahul.backend.entity.Resume;
import com.rahul.backend.repository.ResumeRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final ResumeRepository resumeRepo;

    public ResumeController(ResumeRepository resumeRepo) {
        this.resumeRepo = resumeRepo;
    }

    // ── UPLOAD ──────────────────────────────────────
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {

        // Validate PDF only
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Only PDF files are allowed"));
        }

        // Validate size 10MB max
        if (file.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "File size exceeds 10MB"));
        }

        try {
            // Delete existing resume first (only one resume at a time)
            resumeRepo.findLatest().ifPresent(resumeRepo::delete);

            // Save new resume to database
            Resume resume = new Resume();
            resume.setFilename(
                file.getOriginalFilename() != null
                    ? file.getOriginalFilename()
                    : "resume.pdf"
            );
            resume.setFileData(file.getBytes());
            resume.setContentType("application/pdf");
            resume.setUploadedAt(LocalDateTime.now());
            resumeRepo.save(resume);

            return ResponseEntity.ok(Map.of(
                "message", "Resume uploaded successfully",
                "filename", resume.getFilename()
            ));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Failed to read file"));
        }
    }

    // ── DOWNLOAD ─────────────────────────────────────
    @GetMapping("/download")
    public ResponseEntity<byte[]> download() {
        Optional<Resume> opt = resumeRepo.findLatest();

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Resume resume = opt.get();

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + resume.getFilename() + "\"")
            .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store")
            .body(resume.getFileData());
    }

    // ── STATUS ───────────────────────────────────────
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Optional<Resume> opt = resumeRepo.findLatest();

        if (opt.isEmpty()) {
            return ResponseEntity.ok(Map.of("exists", false));
        }

        Resume resume = opt.get();
        String date = resume.getUploadedAt() != null
            ? resume.getUploadedAt().format(
                DateTimeFormatter.ofPattern("MMM dd, yyyy")
              )
            : "";

        return ResponseEntity.ok(Map.of(
            "exists",     true,
            "filename",   resume.getFilename(),
            "uploadedAt", date
        ));
    }

    // ── DELETE ───────────────────────────────────────
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> delete() {
        Optional<Resume> opt = resumeRepo.findLatest();

        if (opt.isEmpty()) {
            return ResponseEntity.ok(Map.of("deleted", false, "message", "No resume found"));
        }

        resumeRepo.delete(opt.get());
        return ResponseEntity.ok(Map.of("deleted", true, "message", "Resume deleted"));
    }
}
