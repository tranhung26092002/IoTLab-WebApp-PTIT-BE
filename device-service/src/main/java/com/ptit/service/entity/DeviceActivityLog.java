package com.ptit.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "device_activity_logs")
public class DeviceActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "device_id_string")
    private String deviceId; // Device code for easier access

    @Column(name = "activity_type", nullable = false)
    private String activityType; // REGISTRATION, ACTIVATION, DATA_SENT, COMMAND_SENT, COMMAND_EXECUTED, ERROR,
                                 // etc.

    @Column(name = "activity_description")
    private String activityDescription; // Human readable description

    @Column(name = "activity_data", columnDefinition = "TEXT")
    private String activityData; // JSON data related to the activity

    @Column(name = "severity")
    private String severity = "INFO"; // INFO, WARNING, ERROR, CRITICAL

    @Column(name = "source")
    private String source; // MQTT, REST_API, SYSTEM, USER

    @Column(name = "ip_address")
    private String ipAddress; // IP address of the source

    @Column(name = "user_id")
    private Long userId; // User ID if activity was triggered by a user

    @Column(name = "session_id")
    private String sessionId; // Session ID if applicable

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    // Activity types constants
    public static final String ACTIVITY_REGISTRATION = "REGISTRATION";
    public static final String ACTIVITY_ACTIVATION = "ACTIVATION";
    public static final String ACTIVITY_DEACTIVATION = "DEACTIVATION";
    public static final String ACTIVITY_DATA_SENT = "DATA_SENT";
    public static final String ACTIVITY_COMMAND_SENT = "COMMAND_SENT";
    public static final String ACTIVITY_COMMAND_EXECUTED = "COMMAND_EXECUTED";
    public static final String ACTIVITY_COMMAND_FAILED = "COMMAND_FAILED";
    public static final String ACTIVITY_CONFIG_UPDATED = "CONFIG_UPDATED";
    public static final String ACTIVITY_ERROR = "ERROR";
    public static final String ACTIVITY_WARNING = "WARNING";
    public static final String ACTIVITY_RESTART = "RESTART";
    public static final String ACTIVITY_OFFLINE = "OFFLINE";
    public static final String ACTIVITY_ONLINE = "ONLINE";

    // Severity levels
    public static final String SEVERITY_INFO = "INFO";
    public static final String SEVERITY_WARNING = "WARNING";
    public static final String SEVERITY_ERROR = "ERROR";
    public static final String SEVERITY_CRITICAL = "CRITICAL";

    // Sources
    public static final String SOURCE_MQTT = "MQTT";
    public static final String SOURCE_REST_API = "REST_API";
    public static final String SOURCE_SYSTEM = "SYSTEM";
    public static final String SOURCE_USER = "USER";
    public static final String SOURCE_DEVICE = "DEVICE";
}