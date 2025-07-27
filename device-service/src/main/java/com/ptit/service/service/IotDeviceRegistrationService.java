package com.ptit.service.service;

import com.ptit.service.dto.IotDeviceRegistrationDTO;
import com.ptit.service.dto.IotDeviceRegistrationResponseDTO;
import com.ptit.service.entity.Device;
import com.ptit.service.entity.enums.DeviceIotStatus;
import com.ptit.service.entity.enums.DevicePhysicalStatus;
import com.ptit.service.exception.BaseException;
import com.ptit.service.exception.ErrorCode;
import com.ptit.service.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class IotDeviceRegistrationService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ActiveCodeService activeCodeService;

    /**
     * Xử lý đăng ký thiết bị IoT
     */
    public IotDeviceRegistrationResponseDTO registerDevice(IotDeviceRegistrationDTO registrationDTO) {
        log.info("Nhận yêu cầu đăng ký thiết bị: MAC={}, ActiveCode={}",
                registrationDTO.getMacAddress(), registrationDTO.getActiveCode());

        // 1. Validate active code
        if (!isValidActiveCode(registrationDTO.getActiveCode())) {
            log.warn("Active code không hợp lệ: {}", registrationDTO.getActiveCode());
            return new IotDeviceRegistrationResponseDTO("error", "Active code không hợp lệ");
        }

        // 2. Kiểm tra thiết bị đã tồn tại chưa (theo MAC address)
        Optional<Device> existingDevice = deviceRepository.findByMacAddress(registrationDTO.getMacAddress());

        if (existingDevice.isPresent()) {
            Device device = existingDevice.get();
            log.info("Thiết bị đã tồn tại, cập nhật thông tin: deviceId={}", device.getCode());

            // Cập nhật thông tin thiết bị
            updateDeviceInfo(device, registrationDTO);
            deviceRepository.save(device);

            return new IotDeviceRegistrationResponseDTO(
                    device.getCode(),
                    "registered",
                    30 // data interval mặc định
            );
        }

        // 3. Tạo thiết bị mới
        Device newDevice = createNewDevice(registrationDTO);
        deviceRepository.save(newDevice);

        log.info("Tạo thiết bị mới thành công: deviceId={}", newDevice.getCode());

        return new IotDeviceRegistrationResponseDTO(
                newDevice.getCode(),
                "registered",
                30 // data interval mặc định
        );
    }

    /**
     * Kiểm tra active code có hợp lệ không
     */
    private boolean isValidActiveCode(String activeCode) {
        return activeCodeService.isValidActiveCode(activeCode);
    }

    /**
     * Tạo thiết bị mới
     */
    private Device createNewDevice(IotDeviceRegistrationDTO registrationDTO) {
        Device device = new Device();

        // Thông tin cơ bản
        device.setCode(generateDeviceCode());
        device.setName(registrationDTO.getDeviceName());
        device.setType(registrationDTO.getDeviceType());
        device.setDescription(registrationDTO.getDescription());

        // Thông tin IoT
        device.setIotDevice(true);
        device.setActiveCode(registrationDTO.getActiveCode());
        device.setMacAddress(registrationDTO.getMacAddress());
        device.setFirmwareVersion(registrationDTO.getFirmwareVersion());
        device.setWifiSsid(registrationDTO.getWifiSsid());

        // Trạng thái
        device.setPhysicalStatus(DevicePhysicalStatus.AVAILABLE);
        device.setIotStatus(DeviceIotStatus.REGISTERED);

        // Thông tin bổ sung
        device.setDeviceCategory(registrationDTO.getDeviceCategory());
        device.setDifficultyLevel(registrationDTO.getDifficultyLevel());
        device.setMaxUsersPerSession(
                registrationDTO.getMaxUsersPerSession() != null ? registrationDTO.getMaxUsersPerSession() : 1);
        device.setEstimatedDuration(registrationDTO.getEstimatedDuration());

        // Timestamp
        device.setCreatedAt(LocalDateTime.now());
        device.setLastSeen(LocalDateTime.now());

        return device;
    }

    /**
     * Cập nhật thông tin thiết bị
     */
    private void updateDeviceInfo(Device device, IotDeviceRegistrationDTO registrationDTO) {
        device.setName(registrationDTO.getDeviceName());
        device.setType(registrationDTO.getDeviceType());
        device.setDescription(registrationDTO.getDescription());
        device.setFirmwareVersion(registrationDTO.getFirmwareVersion());
        device.setWifiSsid(registrationDTO.getWifiSsid());
        device.setLastSeen(LocalDateTime.now());
        device.setUpdatedAt(LocalDateTime.now());

        // Cập nhật thông tin bổ sung nếu có
        if (registrationDTO.getDeviceCategory() != null) {
            device.setDeviceCategory(registrationDTO.getDeviceCategory());
        }
        if (registrationDTO.getDifficultyLevel() != null) {
            device.setDifficultyLevel(registrationDTO.getDifficultyLevel());
        }
        if (registrationDTO.getMaxUsersPerSession() != null) {
            device.setMaxUsersPerSession(registrationDTO.getMaxUsersPerSession());
        }
        if (registrationDTO.getEstimatedDuration() != null) {
            device.setEstimatedDuration(registrationDTO.getEstimatedDuration());
        }
    }

    /**
     * Tạo device code unique
     */
    private String generateDeviceCode() {
        String prefix = "IOT";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8); // Lấy 4 số cuối
        String random = String.format("%03d", (int) (Math.random() * 1000));
        return prefix + "_" + timestamp + "_" + random;
    }
}