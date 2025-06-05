package com.ptit.service.schedule;

import com.ptit.service.service.BorrowRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutoReturnScheduler {
    private final BorrowRecordService borrowRecordService;

    @Scheduled(cron = "0 0 * * * ?") // Run every hour
    public void autoUpdateOverdueRecords() {
        borrowRecordService.autoUpdateOverdueBorrowRecords();
    }
} 