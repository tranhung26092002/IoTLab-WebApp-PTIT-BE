package com.ptit.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sensor_data")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SensorData {

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

    @Column(name = "led")
    private int led;

    @Column(name = "co2")
    private float co2;

    @Column(name = "gas")
    private float gas;

    @Column(name = "smoke")
    private float smoke;

    @Column(name = "light")
    private float light;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @PrePersist
    public void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
