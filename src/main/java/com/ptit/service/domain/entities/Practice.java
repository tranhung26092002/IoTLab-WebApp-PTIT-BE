package com.ptit.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ptit.service.domain.enums.PracticeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PracticeStatus status;

    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @OneToMany
    @JsonManagedReference
    private List<PracticeVideo> practiceVideos;

    @OneToMany
    @JsonManagedReference
    private List<PracticeFile> practiceFiles;

    @OneToMany
    @JsonManagedReference
    private List<PracticeGuide> practiceGuides;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
