package com.ptit.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "device_sessions")
public class DeviceSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Device device;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    private ClassGroup classGroup;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "session_type")
    private String sessionType; // "PRACTICE", "EXAM", "DEMO"

    @Column(name = "status")
    private String status; // "ACTIVE", "PAUSED", "ENDED"

    @Column(name = "session_data", columnDefinition = "TEXT")
    private String sessionData; // JSON data
}