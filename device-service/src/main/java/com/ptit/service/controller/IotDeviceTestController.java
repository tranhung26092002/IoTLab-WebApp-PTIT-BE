package com.ptit.service.controller;

import com.ptit.service.dto.IotDeviceRegistrationDTO;
import com.ptit.service.dto.IotDeviceRegistrationResponseDTO;
import com.ptit.service.response.DataResponse;
import com.ptit.service.response.MessageResponse;
import com.ptit.service.service.IotDeviceRegistrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test/iot-devices")
@Api(tags = "IoT Device Test", description = "APIs test cho thiết bị IoT")
@Slf4j
public class IotDeviceTestController extends BaseController {

    @Autowired
    private IotDeviceRegistrationService registrationService;

    @PostMapping("/test-registration")
    @ApiOperation("Test đăng ký thiết bị với dữ liệu mẫu")
    public ResponseEntity<DataResponse<IotDeviceRegistrationResponseDTO>> testRegistration() {

        // Tạo dữ liệu test mẫu từ firmware ESP32
        IotDeviceRegistrationDTO testData = new IotDeviceRegistrationDTO();
        testData.setMacAddress("AA:BB:CC:DD:EE:FF");
        testData.setDeviceName("Temperature Sensor Lab A");
        testData.setActiveCode("IOT_ACT_123456789");
        testData.setDeviceType("TEMPERATURE_HUMIDITY_SENSOR");
        testData.setFirmwareVersion("1.0.0");
        testData.setSensors("DHT22,GAS_SENSOR");
        testData.setCapabilities("temperature,humidity,gas");
        testData.setWifiSsid("PTIT_LAB_WIFI");
        testData.setDescription("Cảm biến nhiệt độ và độ ẩm cho phòng lab A");
        testData.setDeviceCategory("SENSOR_KIT");
        testData.setDifficultyLevel("BEGINNER");
        testData.setMaxUsersPerSession(2);
        testData.setEstimatedDuration(30);

        log.info("Test đăng ký thiết bị với dữ liệu mẫu: {}", testData);

        try {
            IotDeviceRegistrationResponseDTO response = registrationService.registerDevice(testData);
            return success(response);
        } catch (Exception e) {
            log.error("Lỗi test đăng ký thiết bị: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(DataResponse.badRequest("Lỗi test đăng ký: " + e.getMessage()));
        }
    }

    @PostMapping("/test-registration-invalid-code")
    @ApiOperation("Test đăng ký thiết bị với active code không hợp lệ")
    public ResponseEntity<DataResponse<IotDeviceRegistrationResponseDTO>> testInvalidCodeRegistration() {

        IotDeviceRegistrationDTO testData = new IotDeviceRegistrationDTO();
        testData.setMacAddress("AA:BB:CC:DD:EE:GG");
        testData.setDeviceName("Invalid Device");
        testData.setActiveCode("INVALID_CODE_123");
        testData.setDeviceType("TEMPERATURE_HUMIDITY_SENSOR");
        testData.setFirmwareVersion("1.0.0");
        testData.setSensors("DHT22");
        testData.setCapabilities("temperature,humidity");
        testData.setWifiSsid("PTIT_LAB_WIFI");

        log.info("Test đăng ký thiết bị với active code không hợp lệ: {}", testData);

        try {
            IotDeviceRegistrationResponseDTO response = registrationService.registerDevice(testData);
            return success(response);
        } catch (Exception e) {
            log.error("Lỗi test đăng ký thiết bị: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(DataResponse.badRequest("Lỗi test đăng ký: " + e.getMessage()));
        }
    }

    @GetMapping("/test-mqtt-status")
    @ApiOperation("Kiểm tra trạng thái MQTT handler")
    public ResponseEntity<DataResponse<MessageResponse>> testMqttStatus() {
        return success(MessageResponse.builder()
                .message("MQTT Registration Handler đang hoạt động và sẵn sàng nhận đăng ký thiết bị")
                .build());
    }
}