package com.ptit.service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ptit.service.entity.enums.DeviceStatus;
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

    @Column(name = "code",nullable = false, unique = true)
    private String code;

    @Column(name = "name",nullable = false)
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

    @Column(name = "current_borrower")
    private Long currentBorrower; // ID người mượn hiện tại (nullable)

    @Column(name = "total_borrowed", nullable = false)
    private int totalBorrowed = 0;

    // IoT Device specific fields
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

    @Column(name = "last_seen")
    private LocalDateTime lastSeen; // Last time device sent data

    @Column(name = "activated_at")
    private LocalDateTime activatedAt; // When device was activated

    @Column(name = "activated_by")
    private Long activatedBy; // User ID who activated the device

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        if (this.code == null) {
            this.code = generateRandomCode();
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    private String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder("Device-");
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @OneToMany(mappedBy = "device")
    @JsonManagedReference
    private List<BorrowRecord> borrowRecords;
}
