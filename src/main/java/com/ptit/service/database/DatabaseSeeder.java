package com.ptit.service.database;


import com.ptit.service.domain.entities.Gateway;
import com.ptit.service.domain.entities.Node;
import com.ptit.service.domain.repositories.GatewayRepository;
import com.ptit.service.domain.repositories.NodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
@RequiredArgsConstructor
public class DatabaseSeeder implements ApplicationRunner {

    // Khai báo các repository cần thiết
    private final GatewayRepository gatewayRepository;
    private final NodeRepository nodeRepository;

    // Khai báo các danh sách cần thiết
    private List<Gateway> gateways;
    private List<Node> nodes;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Khởi tạo các thực thể cần thiết
        gateways = gatewayRepository.findAll();
        nodes = nodeRepository.findAll();

        if (gatewayRepository.count() == 0) genarateGateways();
        if (nodeRepository.count() == 0) genarateNodes();

    }

    private void genarateGateways() {
        gateways = new ArrayList<>();
        gateways.add(new Gateway("Gateway_01", "Gateway 1", "Hanoi", "", false));
        gateways.add(new Gateway("Gateway_02", "Gateway 2", "Hanoi", "", false));

        gatewayRepository.saveAll(gateways);
    }

    private void genarateNodes() {
        nodes = new ArrayList<>();
        nodes.add(new Node("Node_01", "Node 1", false, gateways.get(0)));
        nodes.add(new Node("Node_02", "Node 2", false, gateways.get(0)));

        nodes.add(new Node("Node_03", "Node 3", false, gateways.get(1)));
        nodes.add(new Node("Node_04", "Node 4", false, gateways.get(1)));

        nodeRepository.saveAll(nodes);
    }
}