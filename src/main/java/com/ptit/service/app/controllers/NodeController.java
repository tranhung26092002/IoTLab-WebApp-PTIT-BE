package com.ptit.service.app.controllers;

import com.ptit.service.domain.entities.Node;
import com.ptit.service.domain.services.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nodes")
public class NodeController {

    @Autowired
    private NodeService nodeService;

    @GetMapping
    public List<Node> getAllNodes() {
        return nodeService.getAllNodes();
    }

    @GetMapping("/{id}")
    public Node getNodeById(@PathVariable Long id) {
        return nodeService.getNodeById(id);
    }

    @PostMapping
    public Node saveNode(@RequestBody Node node) {
        return nodeService.saveNode(node);
    }
}