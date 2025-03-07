package com.ptit.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ptit.service.domain.enums.ReportStatus;
import com.ptit.service.domain.enums.ShiftType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reports")
@Where(clause = "is_deleted = false")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "class_group")
    private String classGroup;

    @Column(name = "class_name")
    private String className;

    @Column(name = "shift_type")
    @Enumerated(EnumType.STRING)
    private ShiftType shift;

    @Column(name = "discussion")
    private String discussion;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "report_students",
            joinColumns = @JoinColumn(name = "report_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ReportContent> practiceContents = new ArrayList<>();

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "practice_id", nullable = false)
    private Practice practice;
}
