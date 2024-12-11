package com.ptit.service.domain.services;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaseService {

  protected ModelMapper mapper = new ModelMapper();

//  @Value("${om-farm.sso-service}")
//  protected String ssoService;
//
//  @Autowired SuppliesInventoryLogRepository suppliesInventoryLogRepository;
//  @Autowired BaseCustomRepository baseCustomRepository;
//  @Autowired UserRepository userRepository;
//  @Autowired TrackingQRRepository trackingQRRepository;
//  @Autowired TrackingQrBatchRepository trackingQrBatchRepository;
//  @Autowired Gson gson;
}
