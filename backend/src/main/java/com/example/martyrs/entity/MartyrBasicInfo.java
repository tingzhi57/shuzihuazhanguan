package com.example.martyrs.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "martyr_basic_info")
public class MartyrBasicInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "death_date")
    private LocalDate deathDate;

    private String birthplace;
    private String ethnicity;
    private String politicalStatus;

    @Column(name = "military_unit")
    private String militaryUnit;

    @Column(name = "military_rank")
    @JsonProperty("rank")
    private String militaryRank;

    @Column(name = "sacrifice_location")
    private String sacrificeLocation;

    @Column(name = "sacrifice_reason")
    private String sacrificeReason;

    @Column(name = "burial_location")
    private String burialLocation;

    private String photo;
    private String description;

    private Integer status = 1;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
