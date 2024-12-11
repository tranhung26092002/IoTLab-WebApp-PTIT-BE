package com.ptit.service.domain.services;

import com.ptit.service.app.dtos.auth.OtpCodeDto;
import com.ptit.service.app.dtos.auth.PhoneNumberDto;
import com.ptit.service.app.responses.auth.OTPResponse;
import org.springframework.stereotype.Service;

@Service
public interface OTPService {
    OTPResponse sendOTP(PhoneNumberDto phoneNumberDto);
    boolean verifyOTP(OtpCodeDto otpCodeDto);
    String generateOTP();
}
