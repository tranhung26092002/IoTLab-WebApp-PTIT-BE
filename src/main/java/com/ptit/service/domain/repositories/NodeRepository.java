package com.ptit.service.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ptit.service.domain.entities.Node;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    Optional<Node> findByNodeId(String nodeId);

    List<Node> findByGatewayId(Long id);
}
