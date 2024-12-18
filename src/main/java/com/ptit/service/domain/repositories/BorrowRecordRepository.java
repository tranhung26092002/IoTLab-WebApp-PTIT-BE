package com.ptit.service.domain.repositories;

import com.ptit.service.domain.entities.BorrowRecord;
import com.ptit.service.domain.entities.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    List<BorrowRecord> findByDeviceId(Long deviceId);
    Page<BorrowRecord> findByUserId(Long userId, Pageable pageable);

    Page<BorrowRecord> findAllByDeviceId(Long deviceId, Pageable pageable);

    @Query("SELECT DISTINCT br.device FROM BorrowRecord br WHERE br.userId = :userId")
    Page<Device> findDevicesBorrowedByUser(@Param("userId") Long userId, Pageable pageable);

    BorrowRecord findByDeviceIdAndUserId(Long deviceId, Long userId);
}
