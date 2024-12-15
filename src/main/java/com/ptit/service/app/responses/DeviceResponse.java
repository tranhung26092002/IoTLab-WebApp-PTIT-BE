package com.ptit.service.app.responses;

import lombok.Data;

@Data
public class DeviceResponse {
    private Long id;
    private String deviceId;
    private String name;
    private String type;
    private String location;
}
