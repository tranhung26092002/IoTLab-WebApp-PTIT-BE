package com.ptit.service.config;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@Configuration
@EnableWebSocket
public class MqttConfig {

    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.topic}")
    private String topic;

    private final SimpMessagingTemplate messagingTemplate;

    public MqttConfig(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Bean
    public MqttClient mqttClient() {
        try {
            logger.info("Initializing MQTT client...");
            logger.debug("Broker URL: {}", brokerUrl);
            logger.debug("Client ID: {}", clientId);
            logger.debug("Username: {}", username);

            MqttClient client = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            // Thêm thông tin xác thực vào connect options
            if (username != null && !username.isEmpty()) {
                options.setUserName(username);
                options.setPassword(password.toCharArray());
            }

            client.connect(options);
            logger.info("MQTT client connected successfully to broker at: {}", brokerUrl);

            return client;
        } catch (MqttException e) {
            logger.error("Failed to connect to MQTT broker at: {}", brokerUrl, e);
            throw new RuntimeException("Error initializing MQTT client", e);
        }
    }
}
