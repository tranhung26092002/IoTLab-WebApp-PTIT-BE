package com.ptit.service.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ptit.service.dto.IotDeviceRegistrationDTO;
import com.ptit.service.dto.IotDeviceRegistrationResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class MqttRegistrationHandler implements MqttCallback {

    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private IotDeviceRegistrationService registrationService;

    @Autowired
    private Gson gson;

    @Value("${mqtt.topic.device-register:iot/devices/register}")
    private String deviceRegisterTopic;

    @Value("${mqtt.topic.device-register-response:iot/devices/register/response}")
    private String deviceRegisterResponseTopic;

    @PostConstruct
    public void init() {
        try {
            // Subscribe vào topic đăng ký thiết bị
            mqttClient.subscribe(deviceRegisterTopic);
            mqttClient.setCallback(this);

            log.info("MQTT Registration Handler đã khởi tạo và subscribe vào topic: {}", deviceRegisterTopic);
        } catch (MqttException e) {
            log.error("Lỗi khởi tạo MQTT Registration Handler: {}", e.getMessage(), e);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.warn("Mất kết nối MQTT: {}", cause.getMessage());
        // Có thể implement logic reconnect ở đây
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
        log.info("Nhận MQTT message từ topic {}: {}", topic, payload);

        if (deviceRegisterTopic.equals(topic)) {
            handleDeviceRegistration(payload);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.debug("MQTT message đã được gửi thành công");
    }

    /**
     * Xử lý đăng ký thiết bị từ MQTT
     */
    private void handleDeviceRegistration(String payload) {
        try {
            // Parse JSON payload từ thiết bị
            JsonObject jsonObject = JsonParser.parseString(payload).getAsJsonObject();

            // Map JSON sang DTO
            IotDeviceRegistrationDTO registrationDTO = new IotDeviceRegistrationDTO();
            registrationDTO.setMacAddress(getStringValue(jsonObject, "mac_address"));
            registrationDTO.setDeviceName(getStringValue(jsonObject, "device_name"));
            registrationDTO.setActiveCode(getStringValue(jsonObject, "active_code"));
            registrationDTO.setDeviceType(getStringValue(jsonObject, "device_type"));
            registrationDTO.setFirmwareVersion(getStringValue(jsonObject, "firmware_version"));
            registrationDTO.setSensors(getStringValue(jsonObject, "sensors"));
            registrationDTO.setCapabilities(getStringValue(jsonObject, "capabilities"));
            registrationDTO.setWifiSsid(getStringValue(jsonObject, "wifi_ssid"));

            log.info("Xử lý đăng ký thiết bị: MAC={}, Name={}",
                    registrationDTO.getMacAddress(), registrationDTO.getDeviceName());

            // Gọi service đăng ký
            IotDeviceRegistrationResponseDTO response = registrationService.registerDevice(registrationDTO);

            // Gửi response về thiết bị
            sendRegistrationResponse(response);

        } catch (Exception e) {
            log.error("Lỗi xử lý đăng ký thiết bị: {}", e.getMessage(), e);

            // Gửi response lỗi
            IotDeviceRegistrationResponseDTO errorResponse = new IotDeviceRegistrationResponseDTO(
                    "error", "Lỗi xử lý đăng ký: " + e.getMessage());
            sendRegistrationResponse(errorResponse);
        }
    }

    /**
     * Gửi response đăng ký về thiết bị
     */
    private void sendRegistrationResponse(IotDeviceRegistrationResponseDTO response) {
        try {
            String responseJson = gson.toJson(response);
            MqttMessage message = new MqttMessage(responseJson.getBytes(StandardCharsets.UTF_8));

            mqttClient.publish(deviceRegisterResponseTopic, message);

            log.info("Đã gửi response đăng ký: {}", responseJson);

        } catch (MqttException e) {
            log.error("Lỗi gửi response đăng ký: {}", e.getMessage(), e);
        }
    }

    /**
     * Lấy giá trị string từ JsonObject, trả về null nếu không tồn tại
     */
    private String getStringValue(JsonObject jsonObject, String key) {
        if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
            return jsonObject.get(key).getAsString();
        }
        return null;
    }
}