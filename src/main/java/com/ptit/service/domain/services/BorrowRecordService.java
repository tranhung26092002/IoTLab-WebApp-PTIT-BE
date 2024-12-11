package com.ptit.service.domain.services;

import com.ptit.service.app.responses.BorrowRecordResponse;
import com.ptit.service.app.responses.DeviceReponse;
import com.ptit.service.app.responses.ResponsePage;
import com.ptit.service.domain.entities.BorrowRecord;
import com.ptit.service.domain.entities.Device;
import com.ptit.service.domain.enums.BorrowStatus;
import com.ptit.service.domain.repositories.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowRecordService {
    private final BorrowRecordRepository borrowRecordRepository;
    private final DeviceService deviceService;

    public BorrowRecord createBorrowRecord(Long deviceId, Long userId, String note, LocalDate expiredAt) {
        Device device = deviceService.borrowDevice(deviceId, userId);

        BorrowRecord record = new BorrowRecord();
        record.setDevice(device);
        record.setNote(note);
        record.setExpiredAt(expiredAt);
        record.setUserId(userId);
        record.setStatus(BorrowStatus.BORROWED);

        return borrowRecordRepository.save(record);
    }

    public BorrowRecord returnBorrowRecord(Long deviceId, Long userId) {
        List<BorrowRecord> records = borrowRecordRepository.findByDeviceId(deviceId);
        BorrowRecord activeRecord = records.stream()
                .filter(r -> r.getStatus() == BorrowStatus.BORROWED && r.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No active borrow record found"));

        activeRecord.setReturnedAt(LocalDate.now());
        activeRecord.setStatus(BorrowStatus.RETURNED);
        borrowRecordRepository.save(activeRecord);

        deviceService.returnDevice(deviceId);
        return activeRecord;
    }

    public ResponsePage<BorrowRecordResponse> getBorrowHistoryByUserId(Long userId, Pageable pageable) {
        Page<BorrowRecord> borrowRecords = borrowRecordRepository.findByUserId(userId, pageable);

        Page<BorrowRecordResponse> responsePage = borrowRecords.map(record -> {
            BorrowRecordResponse response = new BorrowRecordResponse();
            response.setId(record.getId());

            DeviceReponse device = new DeviceReponse();
            device.setId(record.getDevice().getId());
            device.setName(record.getDevice().getName());
            device.setCode(record.getDevice().getCode());

            response.setDevice(device);
            response.setUserId(record.getUserId());
            response.setNote(record.getNote());
            response.setBorrowedAt(record.getBorrowedAt());
            response.setExpiredAt(record.getExpiredAt());
            response.setReturnedAt(record.getReturnedAt());
            response.setStatus(record.getStatus());
            return response;
        });

        return new ResponsePage<>(responsePage);
    }


    public ResponsePage<BorrowRecord> getBorrowHistoryByDeviceId(Long deviceId, Pageable pageable) {
        return new ResponsePage<>(borrowRecordRepository.findAllByDeviceId(deviceId, pageable));
    }

    public ResponsePage<Device> getDevicesBorrowedByUser(Long userId, Pageable pageable) {
        Page<Device> devicePage = borrowRecordRepository.findDevicesBorrowedByUser(userId, pageable);
        return new ResponsePage<>(devicePage);
    }
}
