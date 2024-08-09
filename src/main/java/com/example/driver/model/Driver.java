package com.example.driver.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DRIVER")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Driver {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "driver_name")
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
