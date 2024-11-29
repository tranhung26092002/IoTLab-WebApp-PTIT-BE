package com.ptit.service.app.controllers;

import com.ptit.service.domain.services.NodeService;
import com.ptit.service.domain.services.SensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class WebSocketController {

    @Autowired
    private SensorDataService sensorDataService;
    @Autowired
    private NodeService nodeService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Handle incoming messages from the frontend
    @MessageMapping("/send/message")
    @SendTo("/topic/messages")
    public String sendMessage(String message) throws Exception {
        // Xử lý tin nhắn từ client (frontend)
        return message;
    }

    // Broadcast sensor data to the frontend (for automatic updates)
    public void broadcastSensorData(Object sensorData) {
        messagingTemplate.convertAndSend("/topic/sensorData", sensorData);
    }

    // Broadcast active node status to the frontend (for automatic updates)
    public void broadcastActiveNodeStatus(Object activeNodeStatus) {
        messagingTemplate.convertAndSend("/topic/activeNode", activeNodeStatus);
    }
}
