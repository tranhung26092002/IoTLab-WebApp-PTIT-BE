package com.ptit.service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "multiple_choice_options")
public class MultipleChoiceOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonBackReference
    private Question question;

    @Column(name = "option")
    private String option;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_correct")
    private boolean isCorrect;
}