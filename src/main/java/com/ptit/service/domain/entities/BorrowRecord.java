package com.ptit.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ptit.service.domain.enums.BorrowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "borrow_records")
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "device_id", nullable = false, referencedColumnName = "id")
    private Device device;

    @Column(name = "user_id", nullable = false)
    private Long userId; // Lấy từ user-service

    @Column(name = "note")
    private String note;

    @Column(name = "borrowed_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDate borrowedAt = LocalDate.now();

    @Column(name = "expired_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDate expiredAt;

    @Column(name = "returned_at", columnDefinition = "TIMESTAMP")
    private LocalDate returnedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BorrowStatus status = BorrowStatus.BORROWED;
}
