package com.ptit.service.dto;

import com.ptit.service.entity.enums.DeviceStatus;
import lombok.Data;

@Data
public class DeviceFilterDTO {
    private String name;
    private String type;
    private DeviceStatus status;

    private String sortField;
    private String sortOrder;
}
