package com.rahul.backend.repository;

import com.rahul.backend.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    @Query("SELECT r FROM Resume r ORDER BY r.uploadedAt DESC LIMIT 1")
    Optional<Resume> findLatest();
}
