package com.ptit.service.domain.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "gateways")
@NoArgsConstructor
public class Gateway extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "gateway_id",unique = true, nullable = false)
    private String gatewayId;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "gateway", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Node> nodes;

    public Gateway(String gatewayId, String name, String location, String ipAddress, Boolean isActive) {
        this.gatewayId = gatewayId;
        this.name = name;
        this.location = location;
        this.ipAddress = ipAddress;
        this.isActive = isActive;
    }
}
