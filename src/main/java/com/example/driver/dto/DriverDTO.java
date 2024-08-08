package com.example.driver.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DriverDTO(
        Integer id,
        @NotBlank(message = "Name is required") String name,
        @NotBlank(message = "Email is required") String email,
        @NotBlank(message = "License Number is required") String licenseNumber,
        @NotBlank(message = "Vehicle Model is required") String vehicleModel,
        @NotBlank(message = "Vehicle Number is required") String vehicleNumber
) {
}
