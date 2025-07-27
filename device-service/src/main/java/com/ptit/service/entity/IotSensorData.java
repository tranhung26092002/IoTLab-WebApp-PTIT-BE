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

    // Sensor data fields - từ firmware ESP32
    @Column(name = "temperature")
    private Double temperature; // Temperature from DHT22 (°C)

    @Column(name = "humidity")
    private Double humidity; // Humidity from DHT22 (%)

    @Column(name = "gas_value")
    private Integer gasValue; // Gas sensor value (analog)

    @Column(name = "pressure")
    private Double pressure; // Pressure from BMP280 (hPa)

    @Column(name = "light")
    private Double light; // Light sensor value (lux)

    // System status fields - từ firmware ESP32
    @Column(name = "battery_level")
    private Integer batteryLevel; // Battery level (0-100)

    @Column(name = "signal_strength")
    private Integer signalStrength; // WiFi signal strength (RSSI)

    @Column(name = "free_heap")
    private Long freeHeap; // Free heap memory in bytes

    @Column(name = "uptime")
    private Long uptime; // Device uptime in seconds

    @Column(name = "system_status")
    private String systemStatus; // System status (ACTIVE, ERROR, etc.)

    // GPIO control states - từ firmware ESP32
    @Column(name = "led_state")
    private Boolean ledState; // LED control state

    @Column(name = "buzzer_state")
    private Boolean buzzerState; // Buzzer control state

    // Raw data for flexibility
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData; // JSON raw data from sensors

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}