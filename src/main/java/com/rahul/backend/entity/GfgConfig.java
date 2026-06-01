package com.rahul.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "gfg_config")
public class GfgConfig {

    @Id
    private Long id = 1L;

    @Column(columnDefinition = "TEXT")
    private String fullCookieString;

    private String updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullCookieString() {
        return fullCookieString;
    }

    public void setFullCookieString(String fullCookieString) {
        this.fullCookieString = fullCookieString;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
