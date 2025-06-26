package com.ptit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IotDeviceActivationDTO {
    private String activeCode;
    private String deviceName;
    private String description;
    private String location;
    private Integer dataInterval;
    private String alertThresholds;
    private String displayConfig;
} 