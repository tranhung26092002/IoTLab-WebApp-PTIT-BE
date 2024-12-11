package com.ptit.service.domain.services.impl;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.ptit.service.app.dtos.auth.OtpCodeDto;
import com.ptit.service.app.dtos.auth.PhoneNumberDto;
import com.ptit.service.app.responses.auth.OTPResponse;
import com.ptit.service.domain.exceptions.ErrorMessage;
import com.ptit.service.domain.repositories.UserRepository;
import com.ptit.service.domain.services.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    private final UserRepository userRepository;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final int TIME_TO_LIVE_OTP_MINUTE = 5;
    private final int LENGTH_OTP = 6;

    private final String NUMBERS = "0123456789";

    @Override
    @Transactional
    public OTPResponse sendOTP(PhoneNumberDto request) {
        String phoneNumber = request.getPhoneNumber();

        // Check if the phone number already exists
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new ExceptionOm(HttpStatus.CONFLICT, ErrorMessage.PHONE_NUMBER_ALREADY_EXISTS.val());
        }

        // Generate OTP
        String otp = generateOTP();

        // Store OTP in cache
        cache.put(phoneNumber, otp);

        // Schedule OTP removal after TTL
        removeOTP(phoneNumber);

        return OTPResponse.builder()
                .otpCode(otp)
                .build();
    }

    private void removeOTP(String phoneNumber) {
        executorService.submit(() -> {
            try {
                TimeUnit.MINUTES.sleep(TIME_TO_LIVE_OTP_MINUTE);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
            cache.remove(phoneNumber); // Remove OTP from cache after TTL
        });
    }

    @Override
    public boolean verifyOTP(OtpCodeDto otpCodeDto) {
        String phoneNumber = otpCodeDto.getPhoneNumber();
        String otpCode = otpCodeDto.getOtpCode();

        // Verify OTP and remove it from the cache if successful
        if (cache.containsKey(phoneNumber) && cache.get(phoneNumber).equals(otpCode)) {
            cache.remove(phoneNumber);  // Remove OTP after successful verification
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public String generateOTP() {
        StringBuilder otp = new StringBuilder(LENGTH_OTP);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for(int i = 0; i < LENGTH_OTP; ++i){
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
