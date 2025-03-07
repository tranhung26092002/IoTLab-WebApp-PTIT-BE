package com.ptit.service.domain.services;

import com.ptit.service.domain.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface AttendanceService {
    boolean checkAndMarkAttendance(User user);
}
