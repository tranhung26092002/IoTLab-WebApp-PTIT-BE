package com.ptit.service.domain.services;


import com.google.gson.Gson;
import com.ptit.service.domain.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaseService {

    protected ModelMapper mapper = new ModelMapper();

    @Autowired Gson gson;
    @Autowired GatewayRepository gatewayRepository;
    @Autowired NodeRepository nodeRepository;
    @Autowired SensorDataRepository sensorDataRepository;
}
