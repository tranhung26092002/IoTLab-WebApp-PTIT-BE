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

import javax.validation.Valid;

@RestController
@RequestMapping("/api/iot-devices")
@Api(tags = "IoT Device Registration", description = "APIs đăng ký thiết bị IoT")
@Slf4j
public class IotDeviceRegistrationController extends BaseController {

    @Autowired
    private IotDeviceRegistrationService registrationService;

    @PostMapping("/register")
    @ApiOperation("Đăng ký thiết bị IoT (REST fallback)")
    public ResponseEntity<DataResponse<IotDeviceRegistrationResponseDTO>> registerDevice(
            @Valid @RequestBody IotDeviceRegistrationDTO registrationDTO) {

        log.info("Nhận yêu cầu đăng ký thiết bị qua REST: MAC={}", registrationDTO.getMacAddress());

        try {
            IotDeviceRegistrationResponseDTO response = registrationService.registerDevice(registrationDTO);

            if ("error".equals(response.getStatus())) {
                return ResponseEntity.badRequest()
                        .body(DataResponse.badRequest(response.getMessage()));
            }

            return success(response);

        } catch (Exception e) {
            log.error("Lỗi đăng ký thiết bị: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(DataResponse.badRequest("Lỗi đăng ký thiết bị: " + e.getMessage()));
        }
    }

    @PostMapping("/register/mqtt-simulate")
    @ApiOperation("Mô phỏng đăng ký thiết bị qua MQTT (cho testing)")
    public ResponseEntity<DataResponse<IotDeviceRegistrationResponseDTO>> simulateMqttRegistration(
            @Valid @RequestBody IotDeviceRegistrationDTO registrationDTO) {

        log.info("Mô phỏng đăng ký thiết bị qua MQTT: MAC={}", registrationDTO.getMacAddress());

        try {
            // Gọi cùng service như MQTT handler
            IotDeviceRegistrationResponseDTO response = registrationService.registerDevice(registrationDTO);

            if ("error".equals(response.getStatus())) {
                return ResponseEntity.badRequest()
                        .body(DataResponse.badRequest(response.getMessage()));
            }

            return success(response);

        } catch (Exception e) {
            log.error("Lỗi mô phỏng đăng ký thiết bị: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(DataResponse.badRequest("Lỗi mô phỏng đăng ký thiết bị: " + e.getMessage()));
        }
    }

    @GetMapping("/register/status")
    @ApiOperation("Kiểm tra trạng thái đăng ký thiết bị")
    public ResponseEntity<DataResponse<MessageResponse>> getRegistrationStatus() {
        return success(MessageResponse.builder().message("MQTT Registration Handler đang hoạt động").build());
    }
}