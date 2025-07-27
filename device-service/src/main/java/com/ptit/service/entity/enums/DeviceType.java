package com.ptit.service.entity.enums;

public enum DeviceType {
    // Sensor Kits
    TEMPERATURE_HUMIDITY_SENSOR("Cảm biến nhiệt độ và độ ẩm"),
    GAS_SENSOR("Cảm biến khí gas"),
    LIGHT_SENSOR("Cảm biến ánh sáng"),
    PRESSURE_SENSOR("Cảm biến áp suất"),
    MOTION_SENSOR("Cảm biến chuyển động"),

    // Actuator Kits
    LED_CONTROL_KIT("Bộ điều khiển LED"),
    MOTOR_CONTROL_KIT("Bộ điều khiển động cơ"),
    RELAY_CONTROL_KIT("Bộ điều khiển relay"),
    BUZZER_CONTROL_KIT("Bộ điều khiển buzzer"),

    // Full Kits
    SMART_HOME_KIT("Bộ kit nhà thông minh"),
    ENVIRONMENTAL_MONITORING_KIT("Bộ kit giám sát môi trường"),
    AUTOMATION_KIT("Bộ kit tự động hóa"),
    SECURITY_KIT("Bộ kit an ninh"),

    // Custom
    CUSTOM_KIT("Bộ kit tùy chỉnh");

    private final String description;

    DeviceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}