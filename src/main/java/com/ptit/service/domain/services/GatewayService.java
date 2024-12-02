package com.ptit.service.domain.services;

import com.ptit.service.app.responses.MessageResponse;
import com.ptit.service.domain.entities.Gateway;
import com.ptit.service.domain.entities.Node;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GatewayService extends BaseService{

    public List<Gateway> getAllGateways() {
        return gatewayRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Gateway::getId))
                .peek(gateway -> gateway.setNodes(
                        gateway.getNodes()
                                .stream()
                                .sorted(Comparator.comparing(Node::getId))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
    public Gateway getGatewayById(Long id) {
        return gatewayRepository.findById(id).orElse(null);
    }

    public Gateway saveGateway(Gateway gateway) {
        return gatewayRepository.save(gateway);
    }

    public void activeGateway(String gatewayId) {
        Gateway gateway = gatewayRepository.findByGatewayId(gatewayId).orElse(null);

        if (gateway != null) {
            gateway.setActive(true);
            gatewayRepository.save(gateway);
        } else {
            throw new RuntimeException("Gateway not found");
        }
    }
}
