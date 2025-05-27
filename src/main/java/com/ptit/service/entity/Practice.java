package com.ptit.service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ptit.service.entity.enums.PracticeStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public String toString() {
        return "Practice{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", practiceOrder=" + practiceOrder +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
