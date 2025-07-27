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
@Table(name = "iot_device_commands")
public class IotDeviceCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "device_id_string")
    private String deviceId; // Device code for easier access

    @Column(name = "command", nullable = false)
    private String command; // Command type (ACTIVATE, RESTART, LED_CONTROL, etc.)

    @Column(name = "command_data", columnDefinition = "TEXT")
    private String commandData; // JSON data for command parameters

    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, SENT, EXECUTED, FAILED, TIMEOUT

    @Column(name = "sent_at")
    private LocalDateTime sentAt; // When command was sent to device

    @Column(name = "executed_at")
    private LocalDateTime executedAt; // When device executed the command

    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData; // Response from device (JSON)

    @Column(name = "error_message")
    private String errorMessage; // Error message if command failed

    @Column(name = "sent_by")
    private Long sentBy; // User ID who sent the command

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Command types constants
    public static final String COMMAND_ACTIVATE = "ACTIVATE";
    public static final String COMMAND_RESTART = "RESTART";
    public static final String COMMAND_LED_CONTROL = "LED_CONTROL";
    public static final String COMMAND_BUZZER_CONTROL = "BUZZER_CONTROL";
    public static final String COMMAND_GET_STATUS = "GET_STATUS";
    public static final String COMMAND_UPDATE_CONFIG = "UPDATE_CONFIG";

    // Status constants
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_EXECUTED = "EXECUTED";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_TIMEOUT = "TIMEOUT";
}