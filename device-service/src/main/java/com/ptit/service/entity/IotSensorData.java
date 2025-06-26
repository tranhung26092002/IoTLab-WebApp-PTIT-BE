package com.ptit.service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "iot_sensor_data")
public class IotSensorData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "device_id_string")
    private String deviceId; // Device code for easier access

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "humidity")
    private Double humidity;

    @Column(name = "pressure")
    private Double pressure;

    @Column(name = "light")
    private Double light;

    @Column(name = "battery_level")
    private Integer batteryLevel;

    @Column(name = "signal_strength")
    private Integer signalStrength;

    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData; // JSON raw data from sensors

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
} 