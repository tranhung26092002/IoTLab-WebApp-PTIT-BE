package com.ptit.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "password_token")
@Where(clause = "is_used = false")
public class PasswordResetToken {
    private static final long EXPIRATION_TIME_SECONDS = 300_000_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token")
    private String token;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(name = "expiry_date")
    private Timestamp expiryDate;

    @Column(name = "is_used")
    private boolean used;

    @CreationTimestamp
    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING ,pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp createdAt;

    @PrePersist
    private void onPrePersist() {
        this.expiryDate = calculateExpiryDate();
    }

    private Timestamp calculateExpiryDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDateTime = now.plusSeconds(EXPIRATION_TIME_SECONDS);
        return Timestamp.valueOf(expiryDateTime);
    }
}
