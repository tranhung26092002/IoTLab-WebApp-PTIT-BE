package com.ptit.service.app.dtos;

import com.ptit.service.domain.enums.DeviceStatus;
import lombok.Data;

@Data
public class DeviceFilterDto {
    private String name;
    private String type;
    private DeviceStatus status;
    private String sortField;
    private String sortType;
}
