package com.ptit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IotDeviceRegistrationDTO {

    @NotBlank(message = "MAC address không được để trống")
    private String macAddress;

    @NotBlank(message = "Tên thiết bị không được để trống")
    private String deviceName;

    @NotBlank(message = "Active code không được để trống")
    private String activeCode;

    @NotBlank(message = "Loại thiết bị không được để trống")
    private String deviceType;

    @NotBlank(message = "Phiên bản firmware không được để trống")
    private String firmwareVersion;

    @NotBlank(message = "Danh sách cảm biến không được để trống")
    private String sensors;

    @NotBlank(message = "Khả năng thiết bị không được để trống")
    private String capabilities;

    @NotBlank(message = "WiFi SSID không được để trống")
    private String wifiSsid;

    // Optional fields
    private String description;
    private String deviceCategory;
    private String difficultyLevel;
    private Integer maxUsersPerSession;
    private Integer estimatedDuration;
}