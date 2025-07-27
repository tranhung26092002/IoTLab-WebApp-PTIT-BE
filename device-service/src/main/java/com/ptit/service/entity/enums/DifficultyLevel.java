package com.ptit.service.entity.enums;

public enum DifficultyLevel {
    BEGINNER("Người mới bắt đầu", "Phù hợp cho người mới học IoT"),
    INTERMEDIATE("Trung cấp", "Phù hợp cho người đã có kiến thức cơ bản"),
    ADVANCED("Nâng cao", "Phù hợp cho người có kinh nghiệm"),
    EXPERT("Chuyên gia", "Phù hợp cho dự án nghiên cứu và phát triển");

    private final String name;
    private final String description;

    DifficultyLevel(String name, String description) {
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