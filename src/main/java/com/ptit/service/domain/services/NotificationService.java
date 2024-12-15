package com.ptit.service.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendRealtimeUpdate(String topic, Object payload) {
        messagingTemplate.convertAndSend(topic, payload);
    }
}
