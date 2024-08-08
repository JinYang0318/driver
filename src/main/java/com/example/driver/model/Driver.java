package com.example.driver.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@EqualsAndHashCode
@Setter
@Getter
@Table(name = "DRIVER")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Column(name = "vehicle_number")
    private String vehicleNumber;

    @Column(name = "email")
    private String email;
}
