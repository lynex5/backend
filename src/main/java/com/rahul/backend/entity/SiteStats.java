package com.rahul.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SiteStats {

    @Id
    private Long id = 1L; // Always 1
    
    private String nodeName = "rahul-portfolio-v2";
    private String buildDate = "2026.05.13 — stable";
    private String techSummary = "Java · Spring · React · AWS";
    private String lastCommit = "feat: upgraded portfolio UI";
    private int semesters = 4;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    
    public String getBuildDate() { return buildDate; }
    public void setBuildDate(String buildDate) { this.buildDate = buildDate; }
    
    public String getTechSummary() { return techSummary; }
    public void setTechSummary(String techSummary) { this.techSummary = techSummary; }
    
    public String getLastCommit() { return lastCommit; }
    public void setLastCommit(String lastCommit) { this.lastCommit = lastCommit; }
    
    public int getSemesters() { return semesters; }
    public void setSemesters(int semesters) { this.semesters = semesters; }
}
