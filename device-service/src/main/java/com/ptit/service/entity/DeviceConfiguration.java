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
@Table(name = "device_configurations")
public class DeviceConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "device_id_string")
    private String deviceId; // Device code for easier access

    // Data collection configuration
    @Column(name = "data_interval")
    private Integer dataInterval = 30; // Data sending interval in seconds

    @Column(name = "enable_data_collection")
    private Boolean enableDataCollection = true;

    @Column(name = "enable_alerts")
    private Boolean enableAlerts = true;

    // Sensor thresholds
    @Column(name = "temperature_min")
    private Double temperatureMin;

    @Column(name = "temperature_max")
    private Double temperatureMax;

    @Column(name = "humidity_min")
    private Double humidityMin;

    @Column(name = "humidity_max")
    private Double humidityMax;

    @Column(name = "gas_threshold")
    private Integer gasThreshold = 500;

    @Column(name = "light_threshold")
    private Double lightThreshold;

    // GPIO configuration
    @Column(name = "led_pin")
    private Integer ledPin = 14;

    @Column(name = "buzzer_pin")
    private Integer buzzerPin = 12;

    @Column(name = "dht_pin")
    private Integer dhtPin = 4;

    @Column(name = "gas_sensor_pin")
    private Integer gasSensorPin = 36;

    // WiFi configuration
    @Column(name = "wifi_ssid")
    private String wifiSsid;

    @Column(name = "wifi_password")
    private String wifiPassword;

    // MQTT configuration
    @Column(name = "mqtt_broker_url")
    private String mqttBrokerUrl;

    @Column(name = "mqtt_username")
    private String mqttUsername;

    @Column(name = "mqtt_password")
    private String mqttPassword;

    @Column(name = "mqtt_client_id")
    private String mqttClientId;

    // System configuration
    @Column(name = "auto_restart_enabled")
    private Boolean autoRestartEnabled = false;

    @Column(name = "restart_interval_hours")
    private Integer restartIntervalHours = 24;

    @Column(name = "low_battery_threshold")
    private Integer lowBatteryThreshold = 20;

    // Configuration metadata
    @Column(name = "config_version")
    private String configVersion = "1.0";

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "applied_by")
    private Long appliedBy; // User ID who applied this configuration

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
}