package com.rahul.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class About {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String boldStatement;
    private String shortBio;
    private String tagline;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBoldStatement() { return boldStatement; }
    public void setBoldStatement(String boldStatement) { this.boldStatement = boldStatement; }
    public String getShortBio() { return shortBio; }
    public void setShortBio(String shortBio) { this.shortBio = shortBio; }
    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }
}
