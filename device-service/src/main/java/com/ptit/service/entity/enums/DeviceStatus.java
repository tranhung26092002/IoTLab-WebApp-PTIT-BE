package com.ptit.service.entity.enums;

public enum DeviceStatus {
    // Regular device statuses
    AVAILABLE,
    BORROWED,
    
    // IoT device statuses
    REGISTERED,  // Device auto-registered but not activated
    ACTIVE,      // Device activated and showing on dashboard
    OFFLINE,     // Device not sending data
    ERROR,       // Device has error
    DEACTIVATED  // Device deactivated (hidden from dashboard)
}
