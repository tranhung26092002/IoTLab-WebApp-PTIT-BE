package com.ptit.service.service;

import org.modelmapper.ModelMapper;
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
