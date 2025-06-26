package com.ptit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IotDeviceRegistrationDTO {
    private String macAddress;
    private String deviceName;
    private String activeCode;
    private String deviceType;
    private String firmwareVersion;
    private String sensors;
    private String capabilities;
    private String wifiSsid;
} 