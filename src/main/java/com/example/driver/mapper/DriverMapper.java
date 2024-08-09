package com.example.driver.mapper;

import com.example.driver.dto.DriverDTO;
import com.example.driver.model.Driver;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper implements Mapper<Driver, DriverDTO> {

    @Override
    public Driver mapToEntity(DriverDTO driverDTO) {
        return Driver.builder()
                .name(driverDTO.name())
                .email(driverDTO.email())
                .licenseNumber(driverDTO.licenseNumber())
                .vehicleModel(driverDTO.vehicleModel())
                .vehicleNumber(driverDTO.vehicleNumber())
                .build();
    }

    @Override
    public DriverDTO mapToDTO(Driver driver) {
        return DriverDTO.builder()
                .id(driver.getId())
                .name(driver.getName())
                .email(driver.getEmail())
                .licenseNumber(driver.getLicenseNumber())
                .vehicleModel(driver.getVehicleModel())
                .vehicleNumber(driver.getVehicleNumber())
                .build();
    }
}
