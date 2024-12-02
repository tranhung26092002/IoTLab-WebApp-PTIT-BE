package com.ptit.service.app.controllers;

import com.ptit.service.domain.entities.Gateway;
import com.ptit.service.domain.services.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gateways")
public class GatewayController {

    @Autowired
    private GatewayService gatewayService;

    @GetMapping
    public List<Gateway> getAllGateways() {
        return gatewayService.getAllGateways();
    }

    @GetMapping("/{id}")
    public Gateway getGatewayById(@PathVariable Long id) {
        return gatewayService.getGatewayById(id);
    }

    @PutMapping("/{id}")
    public void activeGateway(@PathVariable String id) {
        gatewayService.activeGateway(id);
    }

    @PostMapping
    public Gateway saveGateway(@RequestBody Gateway gateway) {
        return gatewayService.saveGateway(gateway);
    }
}