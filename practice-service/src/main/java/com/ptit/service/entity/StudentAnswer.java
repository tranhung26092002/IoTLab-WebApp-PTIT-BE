package com.ptit.service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;

@Data
@Entity
@Table(name = "student_answer")
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "student_exam_id")
    private StudentExam studentExam;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String essayAnswer; // For essay answers

    private String selectedOption; // For multiple choice answers (A, B, C, D)

    @Type(JsonType.class)
    @Column(name = "image_urls", columnDefinition = "jsonb")
    private List<String> imageUrls;

    private Double score;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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