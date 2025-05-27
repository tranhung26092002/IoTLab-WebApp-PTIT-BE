package com.ptit.service.service;


import com.google.gson.Gson;
import com.ptit.service.repository.DeviceRepository;
import com.ptit.service.repository.SensorDataRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseService {

    protected ModelMapper mapper = new ModelMapper();

    @Autowired Gson gson;
    @Autowired
    DeviceRepository deviceRepository;
    @Autowired
    SensorDataRepository sensorDataRepository;
}
