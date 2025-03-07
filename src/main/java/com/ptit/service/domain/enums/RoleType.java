package com.ptit.service.domain.enums;

public enum RoleType {
    STUDENT,
    TEACHER,
    ADMIN;

    public boolean equalsIgnoreCase(String student) {
        return this.name().equalsIgnoreCase(student);
    }
}
