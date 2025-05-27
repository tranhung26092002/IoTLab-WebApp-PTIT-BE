package com.ptit.service.service;

import com.ptit.service.entity.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserLoginNotificationService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${user.login.exchange}")
    private String userLoginExchange;

    @Value("${user.login.routing.key}")
    private String userLoginRoutingKey;

    public void sendUserLoginNotification(User user) {
        rabbitTemplate.convertAndSend(
                userLoginExchange,
                userLoginRoutingKey,
                user);
    }
} 