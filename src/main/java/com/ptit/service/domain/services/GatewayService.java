package com.ptit.service.domain.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.domain.entities.Gateway;
import com.ptit.service.domain.entities.Node;
import com.ptit.service.domain.entities.SensorData;
import com.ptit.service.domain.repositories.GatewayRepository;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GatewayService extends BaseService{

    public List<Gateway> getAllGateways() {
        return gatewayRepository.findAll();
    }

    public Gateway getGatewayById(Long id) {
        return gatewayRepository.findById(id).orElse(null);
    }

    public Gateway saveGateway(Gateway gateway) {
        return gatewayRepository.save(gateway);
    }

}
