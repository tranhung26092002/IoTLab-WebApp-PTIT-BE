package com.ptit.service.domain.services.impl;

import com.ptit.service.domain.entities.Attendance;
import com.ptit.service.domain.entities.User;
import com.ptit.service.domain.repositories.AttendanceRepository;
import com.ptit.service.domain.services.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;

    @Override
    public boolean checkAndMarkAttendance(User user) {
        // Kiểm tra nếu user không phải là sinh viên thì không ghi nhận điểm danh
        if (user == null || user.getRoleType() == null || !user.getRoleType().equalsIgnoreCase("STUDENT")) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        // Giả sử có 3 ca: sáng (6h-12h), chiều (12h-18h), tối (18h-23h)
        int hour = now.getHour();
        String shift;
        if (hour >= 6 && hour < 12) {
            shift = "MORNING";
        } else if (hour >= 12 && hour < 18) {
            shift = "AFTERNOON";
        } else {
            shift = "EVENING";
        }

        // Kiểm tra xem đã điểm danh trong ca chưa
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); // 00:00:00 hôm nay
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX); // 23:59:59 hôm nay

        boolean alreadyCheckedInToday = attendanceRepository.existsByUserIdAndShiftAndCheckInTimeBetween(
                user.getId(), shift, startOfDay, endOfDay
        );

        if (alreadyCheckedInToday) {
            return false; // Đã điểm danh hôm nay rồi, không cho điểm danh nữa
        }

        // Ghi nhận điểm danh mới
        Attendance attendance = new Attendance( null, user.getId(), user.getUserName(), user.getFullName(), user.getClassCode(), now, shift);
        attendanceRepository.save(attendance);
        return true;
    }
}
