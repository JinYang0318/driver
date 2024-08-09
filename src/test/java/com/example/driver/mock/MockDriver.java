package com.example.driver.mock;

import com.example.driver.dto.DriverDTO;
import com.example.driver.model.Driver;

public class MockDriver {
    public static DriverDTO getDriverDTO() {
        return getDriverDTO(null, "Robert Brown", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123");
    }

    public static DriverDTO getDriverDTO(Integer id, String name, String email, String licenseNumber,
                                         String vehicleModel, String vehicleNumber) {
        return DriverDTO.builder()
                .id(id)
                .name(name)
                .email(email)
                .licenseNumber(licenseNumber)
                .vehicleModel(vehicleModel)
                .vehicleNumber(vehicleNumber)
                .build();
    }

    public static Driver getDriver() {
        return getDriver(null);
    }

    public static Driver getDriver(Integer id) {
        return Driver.builder()
                .id(id)
                .name("Robert Brown")
                .email("robert.brown@example.com")
                .licenseNumber("D1122334")
                .vehicleNumber("LMN9123")
                .vehicleModel("Ford Focus")
                .build();
    }
}
