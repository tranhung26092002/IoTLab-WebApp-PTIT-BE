package com.ptit.service.response;

import com.ptit.service.entity.Device;
import com.ptit.service.entity.enums.DeviceStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IotDeviceResponse {
    private Long id;
    private String code;
    private String name;
    private String type;
    private String description;
    private String activeCode;
    private String macAddress;
    private String ipAddress;
    private String firmwareVersion;
    private String wifiSsid;
    private DeviceStatus status;
    private LocalDateTime lastSeen;
    private LocalDateTime activatedAt;
    private Long activatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static IotDeviceResponse fromDevice(Device device) {
        IotDeviceResponse response = new IotDeviceResponse();
        response.setId(device.getId());
        response.setCode(device.getCode());
        response.setName(device.getName());
        response.setType(device.getType());
        response.setDescription(device.getDescription());
        response.setActiveCode(device.getActiveCode());
        response.setMacAddress(device.getMacAddress());
        response.setIpAddress(device.getIpAddress());
        response.setFirmwareVersion(device.getFirmwareVersion());
        response.setWifiSsid(device.getWifiSsid());
        response.setStatus(device.getStatus());
        response.setLastSeen(device.getLastSeen());
        response.setActivatedAt(device.getActivatedAt());
        response.setActivatedBy(device.getActivatedBy());
        response.setCreatedAt(device.getCreatedAt());
        response.setUpdatedAt(device.getUpdatedAt());
        return response;
    }
} 