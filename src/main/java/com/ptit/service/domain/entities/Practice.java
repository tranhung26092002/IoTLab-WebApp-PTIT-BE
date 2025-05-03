package com.ptit.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ptit.service.domain.enums.PracticeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "practices")
public class Practice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "practice_order")
    private Integer practiceOrder;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PracticeStatus status;

    @OneToMany(mappedBy = "practice", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PracticeVideo> practiceVideos = new ArrayList<>();

    @OneToMany(mappedBy = "practice", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PracticeFile> practiceFiles = new ArrayList<>();

    @OneToMany(mappedBy = "practice", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PracticeGuide> practiceGuides = new ArrayList<>();

    @OneToMany(mappedBy = "practice", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Report> reports = new ArrayList<>();

    @OneToMany(mappedBy = "practice", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<StudentProgress> studentProgresses = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
}
