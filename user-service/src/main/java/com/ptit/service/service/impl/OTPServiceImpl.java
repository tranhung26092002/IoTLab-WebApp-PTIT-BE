package com.ptit.service.service.impl;

import com.ptit.service.dto.EmailDTO;
import com.ptit.service.dto.OtpCodeDTO;
import com.ptit.service.exception.BusinessException;
import com.ptit.service.exception.ErrorCode;
import com.ptit.service.repository.UserRepository;
import com.ptit.service.response.OTPResponse;
import com.ptit.service.service.EmailService;
import com.ptit.service.service.OTPService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final int TIME_TO_LIVE_OTP_MINUTE = 5;
    private final int LENGTH_OTP = 6;
    private final String NUMBERS = "0123456789";

    @Override
    @Transactional
    public OTPResponse sendOTP(EmailDTO request) {
        String email = request.getEmail();

        // Check if the email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(ErrorCode.USER_EMAIL_EXISTS);
        }

        // Generate OTP
        String otp = generateOTP();

        // Store OTP in cache
        cache.put(email, otp);

        // Schedule OTP removal after TTL
        removeOTP(email);

        // Gửi OTP qua email
        try {
            // Tạo context cho Thymeleaf template
            Context context = new Context();
            context.setVariable("otp", otp);

            // Gửi email
            emailService.sendEmail(email, "Your OTP Code", "otp-template", context);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OTP_NOT_SEND);
        }

        return OTPResponse.builder()
                .otpCode(otp)
                .build();
    }

    private void removeOTP(String email) {
        executorService.submit(() -> {
            try {
                TimeUnit.MINUTES.sleep(TIME_TO_LIVE_OTP_MINUTE);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            cache.remove(email); // Remove OTP from cache after TTL
        });
    }

    @Override
    public boolean verifyOTP(OtpCodeDTO otpCodeDto) {
        String email = otpCodeDto.getEmail();
        String otpCode = otpCodeDto.getOtpCode();

        // Verify OTP and remove it from the cache if successful
        if (cache.containsKey(email) && cache.get(email).equals(otpCode)) {
            cache.remove(email);  // Remove OTP after successful verification
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public String generateOTP() {
        StringBuilder otp = new StringBuilder(LENGTH_OTP);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < LENGTH_OTP; ++i) {
            otp.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        return otp.toString();
    }

    // Gracefully shutdown the executor service when the application is stopped
    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
