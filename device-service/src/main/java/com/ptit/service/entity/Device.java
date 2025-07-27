package com.ptit.service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ptit.service.entity.enums.DeviceStatus;
import com.ptit.service.entity.enums.DevicePhysicalStatus;
import com.ptit.service.entity.enums.DeviceIotStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeviceStatus status = DeviceStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "physical_status")
    private DevicePhysicalStatus physicalStatus = DevicePhysicalStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "iot_status")
    private DeviceIotStatus iotStatus = DeviceIotStatus.REGISTERED;

    @Column(name = "device_category")
    private String deviceCategory; // "SENSOR_KIT", "ACTUATOR_KIT", "FULL_KIT"

    @Column(name = "difficulty_level")
    private String difficultyLevel; // "BEGINNER", "INTERMEDIATE", "ADVANCED"

    @Column(name = "max_users_per_session")
    private Integer maxUsersPerSession = 1;

    @Column(name = "estimated_duration")
    private Integer estimatedDuration; // minutes

    @Column(name = "current_borrower")
    private Long currentBorrower; // ID người mượn hiện tại (nullable)

    @Column(name = "total_borrowed", nullable = false)
    private int totalBorrowed = 0;

    // IoT Device specific fields - từ firmware ESP32
    @Column(name = "is_iot_device", nullable = false)
    private boolean isIotDevice = false;

    @Column(name = "active_code", unique = true)
    private String activeCode; // Active Code for IoT devices

    @Column(name = "mac_address", unique = true)
    private String macAddress; // MAC address of ESP32

    @Column(name = "ip_address")
    private String ipAddress; // Current IP address

    @Column(name = "firmware_version")
    private String firmwareVersion; // Firmware version

    @Column(name = "wifi_ssid")
    private String wifiSsid; // WiFi SSID configured

    @Column(name = "sensors", columnDefinition = "TEXT")
    private String sensors; // Comma-separated list of sensors (DHT22,GAS_SENSOR)

    @Column(name = "capabilities", columnDefinition = "TEXT")
    private String capabilities; // Comma-separated list of capabilities (temperature,humidity,gas)

    @Column(name = "data_interval")
    private Integer dataInterval = 30; // Data sending interval in seconds

    @Column(name = "last_seen")
    private LocalDateTime lastSeen; // Last time device sent data

    @Column(name = "activated_at")
    private LocalDateTime activatedAt; // When device was activated

    @Column(name = "activated_by")
    private Long activatedBy; // User ID who activated the device

    // System status fields - từ firmware ESP32
    @Column(name = "battery_level")
    private Integer batteryLevel; // Current battery level (0-100)

    @Column(name = "signal_strength")
    private Integer signalStrength; // WiFi signal strength (RSSI)

    @Column(name = "free_heap")
    private Long freeHeap; // Free heap memory in bytes

    @Column(name = "uptime")
    private Long uptime; // Device uptime in seconds

    @Column(name = "system_status")
    private String systemStatus = "ACTIVE"; // System status from firmware

    // GPIO control fields - từ firmware ESP32
    @Column(name = "led_state")
    private Boolean ledState = false; // LED control state

    @Column(name = "buzzer_state")
    private Boolean buzzerState = false; // Buzzer control state

    @Column(name = "gas_threshold")
    private Integer gasThreshold = 500; // Gas sensor threshold

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        if (this.code == null) {
            this.code = generateRandomCode();
        }
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private String generateRandomCode() {
        String prefix = "IOT";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8); // Lấy 4 số cuối
        String random = String.format("%03d", (int)(Math.random() * 1000));
        return prefix + "_" + timestamp + "_" + random;
    }

    @OneToMany(mappedBy = "device")
    private List<IotSensorData> iotSensorData;

    @OneToMany(mappedBy = "device")
    @JsonManagedReference
    private List<BorrowRecord> borrowRecords;

    @OneToOne(mappedBy = "device")
    private DeviceSession currentSession;

    @OneToMany(mappedBy = "device")
    private List<IotDeviceCommand> commands;
}
