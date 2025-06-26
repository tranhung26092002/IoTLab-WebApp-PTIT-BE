package com.ptit.service.response;

import com.ptit.service.entity.enums.DeviceStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceResponse {
    private Long id;
    private String code;
    private String name;
    private String type;
    private String description;
    private DeviceStatus status;
    private boolean isIotDevice;
    private String activeCode;
    private String macAddress;
    private String firmwareVersion;
    private String wifiSsid;
    private LocalDateTime lastSeen;
    private LocalDateTime activatedAt;
    private LocalDateTime createdAt;
} 