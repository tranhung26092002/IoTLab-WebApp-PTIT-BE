package com.ptit.service.service;

import com.ptit.service.dto.EmailDTO;
import com.ptit.service.dto.OtpCodeDTO;
import com.ptit.service.response.OTPResponse;
import org.springframework.stereotype.Service;

@Service
public interface OTPService {
    OTPResponse sendOTP(EmailDTO emailDto);
    boolean verifyOTP(OtpCodeDTO otpCodeDto);
    String generateOTP();
}
