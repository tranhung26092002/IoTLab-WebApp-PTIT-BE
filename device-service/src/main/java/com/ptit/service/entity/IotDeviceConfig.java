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
@Table(name = "iot_device_config")
public class IotDeviceConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "data_interval", nullable = false)
    private Integer dataInterval = 30; // Seconds

    @Column(name = "sensor_enabled", columnDefinition = "TEXT")
    private String sensorEnabled; // JSON array of enabled sensors

    @Column(name = "alert_thresholds", columnDefinition = "TEXT")
    private String alertThresholds; // JSON object of alert thresholds

    @Column(name = "display_config", columnDefinition = "TEXT")
    private String displayConfig; // JSON object of display configuration

    @Column(name = "wifi_credentials", columnDefinition = "TEXT")
    private String wifiCredentials; // Encrypted JSON WiFi credentials

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 