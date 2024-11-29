package com.ptit.service.domain.services;

import com.ptit.service.domain.entities.Gateway;
import com.ptit.service.domain.entities.Node;
import com.ptit.service.domain.entities.SensorData;
import com.ptit.service.domain.repositories.NodeRepository;
import com.ptit.service.domain.repositories.SensorDataRepository;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MqttService extends BaseService{

    private final MqttClient mqttClient;
    private final SimpMessagingTemplate messagingTemplate;

    private final SensorDataService sensorDataService;

    @PostConstruct
    public void init() {
        try {
            mqttClient.subscribe("mqtt/active/node", this::handleActiveNodeMessage);
            mqttClient.subscribe("mqtt/data/gateway", this::handleGatewayDataMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void handleActiveNodeMessage(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(payload);
            String nameNode = jsonNode.get("nameNode").asText();
            boolean isActive = jsonNode.get("isActive").asBoolean();

            Node node = nodeRepository.findByNodeId(nameNode).orElse(null);
            if (node != null) {
                node.setActive(isActive);
                nodeRepository.save(node);

                // Publish notification to gateway
                MqttMessage notificationMessage = new MqttMessage(payload.getBytes());
                mqttClient.publish("mqtt/notification/gateway", notificationMessage);

                // Send WebSocket message
                messagingTemplate.convertAndSend("/topic/activeNode", payload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGatewayDataMessage(String topic, MqttMessage message) {
        try {
            // Chuyển đổi payload thành chuỗi
            String payload = new String(message.getPayload());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(payload);
            // Lấy tên của Gateway
            String nameGateway = jsonNode.get("nameGateway").asText();
            // Lấy mảng dữ liệu của các Node
            JsonNode dataArray = jsonNode.get("data");

            for (JsonNode dataNode : dataArray) {
                // Lấy tên của Node
                String nameNode = dataNode.get("nameNode").asText();

                // Lấy dữ liệu từ Node, ví dụ: temp, hum, status
                float temperature = dataNode.has("temp") ? dataNode.get("temp").floatValue() : 0.0f;
                float humidity = dataNode.has("hum") ? dataNode.get("hum").floatValue() : 0.0f;
                int status = dataNode.has("status") ? dataNode.get("status").intValue() : 0;
                float light = dataNode.has("light") ? dataNode.get("light").floatValue() : 0.0f;
                float gas = dataNode.has("gas") ? dataNode.get("gas").floatValue() : 0.0f;
                float smoke = dataNode.has("smoke") ? dataNode.get("smoke").floatValue() : 0.0f;
                float co2 = dataNode.has("co2") ? dataNode.get("co2").floatValue() : 0.0f;

                Node node = nodeRepository.findByNodeId(nameNode).orElse(null);
                if (node != null) {
                    SensorData sensorData = new SensorData();
                    sensorData.setNode(node);
                    sensorData.setTemperature(temperature);
                    sensorData.setHumidity(humidity);
                    sensorData.setLight(light);
                    sensorData.setGas(gas);
                    sensorData.setSmoke(smoke);
                    sensorData.setCo2(co2);

                    sensorDataService.saveSensorData(sensorData);

                    // Send WebSocket message
                    messagingTemplate.convertAndSend("/topic/sensorData", sensorData);                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 60000) // Thực thi mỗi 10 giây
    public void publishNodeStatusToGateway() {
        try {
            // Lấy danh sách tất cả các node từ cơ sở dữ liệu
            List<Node> nodes = nodeRepository.findAll();

            for (Node node : nodes) {
                // Chuẩn bị payload dưới dạng JSON
                String payload = String.format(
                        "{\"nameNode\": \"%s\", \"isActive\": %b}",
                        node.getNodeId(),
                        node.isActive()
                );

                // Gửi trạng thái của node đến topic "mqtt/notification/gateway"
                MqttMessage notificationMessage = new MqttMessage(payload.getBytes());
                mqttClient.publish("mqtt/notification/gateway", notificationMessage);

                // Log để kiểm tra
                System.out.printf("Published node status: %s\n", payload);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}