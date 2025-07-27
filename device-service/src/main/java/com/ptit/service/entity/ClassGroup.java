package com.ptit.service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "class_groups")
public class ClassGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "course_code")
    private String courseCode;

    @Column(name = "instructor_id")
    private String instructorId;

    @OneToMany(mappedBy = "classGroup")
    private List<DeviceSession> sessions;
}