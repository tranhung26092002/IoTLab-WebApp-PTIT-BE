package com.ptit.service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class ActiveCodeService {

    // Danh sách active codes hợp lệ (có thể mở rộng để lưu trong database)
    private static final Set<String> VALID_ACTIVE_CODES = new HashSet<>(Arrays.asList(
            "IOT_ACT_123456789",
            "IOT_ACT_987654321",
            "IOT_ACT_111222333",
            "IOT_ACT_444555666",
            "IOT_ACT_777888999",
            "IOT_ACT_000111222"));

    /**
     * Kiểm tra active code có hợp lệ không
     */
    public boolean isValidActiveCode(String activeCode) {
        if (activeCode == null || activeCode.trim().isEmpty()) {
            log.warn("Active code rỗng hoặc null");
            return false;
        }

        String trimmedCode = activeCode.trim();
        boolean isValid = VALID_ACTIVE_CODES.contains(trimmedCode);

        if (isValid) {
            log.info("Active code hợp lệ: {}", trimmedCode);
        } else {
            log.warn("Active code không hợp lệ: {}", trimmedCode);
        }

        return isValid;
    }

    /**
     * Thêm active code mới
     */
    public boolean addActiveCode(String activeCode) {
        if (activeCode == null || activeCode.trim().isEmpty()) {
            log.warn("Không thể thêm active code rỗng");
            return false;
        }

        String trimmedCode = activeCode.trim();
        boolean added = VALID_ACTIVE_CODES.add(trimmedCode);

        if (added) {
            log.info("Đã thêm active code mới: {}", trimmedCode);
        } else {
            log.warn("Active code đã tồn tại: {}", trimmedCode);
        }

        return added;
    }

    /**
     * Xóa active code
     */
    public boolean removeActiveCode(String activeCode) {
        if (activeCode == null || activeCode.trim().isEmpty()) {
            log.warn("Không thể xóa active code rỗng");
            return false;
        }

        String trimmedCode = activeCode.trim();
        boolean removed = VALID_ACTIVE_CODES.remove(trimmedCode);

        if (removed) {
            log.info("Đã xóa active code: {}", trimmedCode);
        } else {
            log.warn("Active code không tồn tại để xóa: {}", trimmedCode);
        }

        return removed;
    }

    /**
     * Lấy danh sách tất cả active codes (chỉ cho admin)
     */
    public Set<String> getAllActiveCodes() {
        return new HashSet<>(VALID_ACTIVE_CODES);
    }

    /**
     * Tạo active code mới theo pattern
     */
    public String generateActiveCode() {
        String prefix = "IOT_ACT_";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8); // Lấy 4 số cuối
        String random = String.format("%03d", (int) (Math.random() * 1000));
        String newCode = prefix + timestamp + random;

        // Đảm bảo code không trùng
        while (VALID_ACTIVE_CODES.contains(newCode)) {
            random = String.format("%03d", (int) (Math.random() * 1000));
            newCode = prefix + timestamp + random;
        }

        VALID_ACTIVE_CODES.add(newCode);
        log.info("Đã tạo active code mới: {}", newCode);

        return newCode;
    }
}