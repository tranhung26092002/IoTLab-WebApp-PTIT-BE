package com.ptit.service.service;

import com.ptit.service.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface AttendanceService {
    boolean checkAndMarkAttendance(User user);
}
