package com.ptit.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "report_contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String content;
    private String performer;
    private String imageUrl;
    private Double evaluation;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "report_id", referencedColumnName = "id", nullable = false)
    private Report report;
}
