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
@Table(name = "iot_device_commands")
public class IotDeviceCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "command_type", nullable = false)
    private String commandType; // ACTIVATE, RESTART, UPDATE_FIRMWARE, CONFIGURE, DEACTIVATE

    @Column(name = "command_data", columnDefinition = "TEXT")
    private String commandData; // JSON command data

    @Column(name = "status", nullable = false)
    private String status = "PENDING"; // PENDING, EXECUTING, COMPLETED, FAILED

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Column(name = "response", columnDefinition = "TEXT")
    private String response; // Response from device

    @Column(name = "created_by")
    private Long createdBy; // User ID who created the command

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
} 