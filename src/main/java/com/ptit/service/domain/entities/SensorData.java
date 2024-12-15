package com.ptit.service.domain.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ptit.service.domain.enums.SensorType;
import com.ptit.service.domain.enums.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sensor_data")
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "device_id")
    private Device device;

    @Column(name = "temperature")
    private float temperature;

    @Column(name = "humidity")
    private float humidity;

    @Column(name = "light")
    private float light;

    @Column(name = "gas")
    private float gas;

    @Column(name = "alert_led")
    private int alertLed;

    @Column(name = "buzzer")
    private int buzzer;

    @Column(name = "led")
    private int led;

    @Column(name = "fan")
    private int fan;

    @Column(name = "servo")
    private int servo;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
}