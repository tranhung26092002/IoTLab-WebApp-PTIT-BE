package com.ptit.service.domain.services;

import com.ptit.service.domain.entities.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserNotificationService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${user.exchange}")
    private String userExchange;

    @Value("${user.created.routing.key}")
    private String userCreatedRoutingKey;

    public void sendUserCreatedNotification(User user) {
        rabbitTemplate.convertAndSend(
                userExchange,
                userCreatedRoutingKey,
                user);
    }
}