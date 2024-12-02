package com.ptit.service.domain.services;

import com.ptit.service.domain.entities.Node;
import com.ptit.service.domain.repositories.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodeService {

    @Autowired
    private NodeRepository nodeRepository;

    public List<Node> getAllNodes() {
        return nodeRepository.findAll();
    }

    public Node getNodeById(Long id) {
        return nodeRepository.findById(id).orElse(null);
    }

    public Node saveNode(Node node) {
        return nodeRepository.save(node);
    }

    public List<Node> getNodesByGateway(Long gatewayId) {
        return nodeRepository.findAllByGatewayId(gatewayId)
                .stream()
                .sorted(Comparator.comparing(Node::getId))
                .collect(Collectors.toList());
    }

    public void activeNode(String id) {
        Node node = nodeRepository.findByNodeId(id).orElse(null);
        if (node != null) {
            node.setActive(!node.isActive());
            nodeRepository.save(node);
        }
    }
}
