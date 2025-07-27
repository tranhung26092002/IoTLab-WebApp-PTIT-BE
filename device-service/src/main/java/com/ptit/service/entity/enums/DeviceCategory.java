package com.ptit.service.entity.enums;

public enum DeviceCategory {
    SENSOR_KIT("Bộ kit cảm biến", "Các bộ kit chứa cảm biến để thu thập dữ liệu"),
    ACTUATOR_KIT("Bộ kit điều khiển", "Các bộ kit chứa thiết bị điều khiển"),
    FULL_KIT("Bộ kit hoàn chỉnh", "Bộ kit kết hợp cảm biến và điều khiển"),
    LEARNING_KIT("Bộ kit học tập", "Bộ kit dành cho mục đích học tập và thực hành"),
    DEMO_KIT("Bộ kit demo", "Bộ kit dùng để trình diễn và demo");

    private final String name;
    private final String description;

    DeviceCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}