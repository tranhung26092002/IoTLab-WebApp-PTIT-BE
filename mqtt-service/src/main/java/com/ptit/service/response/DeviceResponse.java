package com.ptit.service.response;

import lombok.Data;

@Data
public class DeviceResponse {
    private Long id;
    private String deviceId;
    private String name;
    private String type;
    private String location;
    private String wifi;
    private String ip;
}
