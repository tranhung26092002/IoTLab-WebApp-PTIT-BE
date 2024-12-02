package com.ptit.service.app.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.domain.entities.Node;
import com.ptit.service.domain.entities.SensorData;
import com.ptit.service.domain.services.NodeService;
import com.ptit.service.domain.services.SensorDataService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@Slf4j
public class WebSocketController {

    @Autowired
    private SensorDataService sensorDataService;
    @Autowired
    private NodeService nodeService;

    @Autowired
    private MqttClient mqttClient;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Subscribe to specific node data
    @MessageMapping("/node/{nodeId}")
    @SendTo("/topic/sensorData/{nodeId}")
    public SensorData getNodeData(@DestinationVariable Long nodeId) {
        // Get latest sensor data for the specific node
        return sensorDataService.getLatestDataForNode(nodeId);
    }

    // Subscribe to real-time updates for a specific node
    @MessageMapping("/subscribe/node/{nodeId}")
    public void subscribeToNode(@DestinationVariable Long nodeId) {
        Node node = nodeService.getNodeById(nodeId);
        if (node != null) {
            messagingTemplate.convertAndSend(
                "/topic/sensorData/" + nodeId,
                sensorDataService.getLatestDataForNode(nodeId)
            );
        }
    }

    // Get historical data for a node
    @MessageMapping("/node/history/{nodeId}")
    @SendTo("/topic/history/{nodeId}")
    public List<SensorData> getNodeHistory(@DestinationVariable Long nodeId) {
        return sensorDataService.getNodeHistory(nodeId);
    }

    @MessageMapping("/publish/command/{nodeId}")
    public void publishCommand(@DestinationVariable Long nodeId, @Payload String commandJson) {
        try {
            // Kiểm tra node
            Node node = nodeService.getNodeById(nodeId);
            if (node == null) {
                log.warn("Node with ID " + nodeId + " not found.");
                return;
            }

            // Parse outer JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootCommand = mapper.readTree(commandJson);

            // Kiểm tra và trích xuất inner JSON
            if (!rootCommand.has("command")) {
                log.warn("Missing 'command' field in payload: " + commandJson);
                return;
            }

            String innerCommandJson = rootCommand.get("command").asText();
            JsonNode innerCommand = mapper.readTree(innerCommandJson);

            // Log thông tin command
            log.info("Node ID: " + nodeId + ", Parsed command: " + innerCommand.toString());

            // Chuẩn bị và gửi MQTT message
            MqttMessage notificationMessage = new MqttMessage(innerCommand.toString().getBytes(StandardCharsets.UTF_8));
            mqttClient.publish(
                    "mqtt/remote/gateway", // Chủ đề động theo nodeId
                    notificationMessage
            );

            log.info("Command published to MQTT topic 'mqtt/remote/gateway/" + nodeId + "'");
        } catch (JsonProcessingException e) {
            log.error("Error parsing JSON: " + commandJson, e);
        } catch (MqttException e) {
            log.error("Error publishing MQTT message for node ID " + nodeId, e);
        } catch (Exception e) {
            log.error("Unexpected error while processing command for node ID " + nodeId, e);
        }
    }
}
