package com.rahul.backend.config;

import com.rahul.backend.entity.About;
import com.rahul.backend.entity.Contact;
import com.rahul.backend.entity.SiteStats;
import com.rahul.backend.entity.TimelineItem;
import com.rahul.backend.repository.AboutRepository;
import com.rahul.backend.repository.AdminUserRepository;
import com.rahul.backend.repository.ContactRepository;
import com.rahul.backend.repository.SiteStatsRepository;
import com.rahul.backend.repository.TimelineRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(
        AdminUserRepository userRepo,
        AboutRepository aboutRepo,
        ContactRepository contactRepo,
        SiteStatsRepository statsRepo,
        TimelineRepository timelineRepo
    ) {
        return args -> {

            // ── ADMIN USER ──────────────────────────────────────
            // Admin already exists in DB — no action needed
            if (userRepo.count() > 0) {
                System.out.println("✅ Admin user loaded from database.");
            } else {
                System.out.println("⚠️ WARNING: No admin user found in database!");
            }

            // ── ABOUT (seed only if empty) ──────────────────────
            if (aboutRepo.count() == 0) {
                About about = new About();
                about.setBoldStatement("I build things that work, then make them better.");
                about.setShortBio("CS Engineering student at KL University, Vijayawada. I focus on algorithmic problem solving, cloud architecture, and backend systems.\n\nWhen I'm not writing Java or Spring Boot APIs, I document my process through technical content creation on YouTube.");
                about.setTagline("CS ENGINEERING STUDENT · JAVA · CLOUD · SPRING BOOT");
                aboutRepo.save(about);
            }

            // ── CONTACT (seed only if empty) ────────────────────
            if (contactRepo.count() == 0) {
                Contact contact = new Contact();
                contact.setEmail("rahul@kluniversity.edu.in");
                contactRepo.save(contact);
            }

            // ── STATS (seed only if empty) ──────────────────────
            if (statsRepo.count() == 0) {
                SiteStats stats = new SiteStats();
                statsRepo.save(stats);
            }

            // ── TIMELINE (seed only if empty) ───────────────────
            if (timelineRepo.count() == 0) {
                TimelineItem t1 = new TimelineItem();
                t1.setYear("2022"); t1.setEvent("Started B.Tech CSE at KL University"); t1.setSortOrder(1);

                TimelineItem t2 = new TimelineItem();
                t2.setYear("2023"); t2.setEvent("First Java + Spring Boot project"); t2.setSortOrder(2);

                TimelineItem t3 = new TimelineItem();
                t3.setYear("2024"); t3.setEvent("Explored AWS, Docker, Kubernetes"); t3.setSortOrder(3);

                TimelineItem t4 = new TimelineItem();
                t4.setYear("2025"); t4.setEvent("Built full-stack portfolio with React"); t4.setSortOrder(4);

                TimelineItem t5 = new TimelineItem();
                t5.setYear("2026"); t5.setEvent("Actively seeking internships"); t5.setSortOrder(5);

                timelineRepo.save(t1);
                timelineRepo.save(t2);
                timelineRepo.save(t3);
                timelineRepo.save(t4);
                timelineRepo.save(t5);
            }
        };
    }
}
