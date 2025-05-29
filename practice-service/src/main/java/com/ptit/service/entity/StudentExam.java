package com.ptit.service.entity;

import com.ptit.service.entity.enums.ExamStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "student_exam")
public class StudentExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private ExamStatus status;

    private Double score;

    @OneToMany(mappedBy = "studentExam", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentAnswer> answers = new ArrayList<>();
}