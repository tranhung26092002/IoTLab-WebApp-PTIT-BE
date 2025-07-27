package com.ptit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IotDeviceRegistrationResponseDTO {

    private String deviceId;
    private String status;
    private Integer dataInterval;
    private Long serverTime;
    private String message;

    public IotDeviceRegistrationResponseDTO(String deviceId, String status, Integer dataInterval) {
        this.deviceId = deviceId;
        this.status = status;
        this.dataInterval = dataInterval;
        this.serverTime = System.currentTimeMillis();
    }

    public IotDeviceRegistrationResponseDTO(String status, String message) {
        this.status = status;
        this.message = message;
        this.serverTime = System.currentTimeMillis();
    }
}