package com.ptit.service.entity.enums;

public enum DeviceIotStatus {
    REGISTERED, // Đã đăng ký nhưng chưa kích hoạt
    ACTIVE, // Đang hoạt động
    OFFLINE, // Mất kết nối
    ERROR, // Lỗi kỹ thuật
    DEACTIVATED // Tạm ngưng hoạt động
}