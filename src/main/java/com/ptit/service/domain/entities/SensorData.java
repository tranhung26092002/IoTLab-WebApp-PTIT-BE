package com.ptit.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "sensor_data")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SensorData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "node_id", nullable = false)
    private Node node;

    @Column(name = "temperature")
    private float temperature;

    @Column(name = "humidity")
    private float humidity;

    @Column(name = "co2")
    private float co2;

    @Column(name = "gas")
    private float gas;

    @Column(name = "smoke")
    private float smoke;

    @Column(name = "light")
    private float light;
}
