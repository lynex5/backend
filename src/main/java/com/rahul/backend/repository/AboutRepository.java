package com.rahul.backend.repository;

import com.rahul.backend.entity.About;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AboutRepository extends JpaRepository<About, Long> {
}
