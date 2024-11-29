package com.ptit.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "nodes")
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@Where(clause = "is_active = true")
public class Node extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "node_id",unique = true, nullable = false)
    private String nodeId;

    @Column(name = "name")
    private String name;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "gateway_id", nullable = false)
    private Gateway gateway;

    public Node(String nodeId, String name, boolean isActive, Gateway gateway) {
        this.nodeId = nodeId;
        this.name = name;
        this.isActive = isActive;
        this.gateway = gateway;
    }
}
