package com.ptit.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IotSensorDataDTO {
    private String deviceId;
    private LocalDateTime timestamp;
    private Map<String, Object> sensors;
    private Map<String, Object> system;
} 