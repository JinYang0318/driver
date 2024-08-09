package com.example.driver.service;

import com.example.driver.dto.DriverDTO;
import com.example.driver.exception.UniqueException;
import com.example.driver.mapper.Mapper;
import com.example.driver.model.Driver;
import com.example.driver.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final Mapper<Driver, DriverDTO> driverMapper;

    public Optional<DriverDTO> getDriverById(Integer id) {
        return driverRepository.findById(id)
                .map(driverMapper::mapToDTO);
    }

    public List<DriverDTO> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(driverMapper::mapToDTO)
                .toList();
    }

    public List<DriverDTO> getDriverByIds(List<Integer> ids) {
        return driverRepository.findAllById(ids)
                .stream()
                .map(driverMapper::mapToDTO)
                .toList();
    }

    public Optional<DriverDTO> createDriver(DriverDTO driverDTO) {
        checkFieldUniqueness(driverDTO);
        return Optional.of(driverRepository.save(driverMapper.mapToEntity(driverDTO)))
                .map(driverMapper::mapToDTO);
    }

    public Optional<DriverDTO> updateDriver(Integer id, DriverDTO driverDTO) {
        return driverRepository.findById(id)
                .map(existingDriver -> {
                    existingDriver.setName(driverDTO.name());
                    existingDriver.setLicenseNumber(driverDTO.licenseNumber());
                    existingDriver.setEmail(driverDTO.email());
                    existingDriver.setVehicleModel(driverDTO.vehicleModel());
                    existingDriver.setVehicleNumber(driverDTO.vehicleNumber());

                    checkFieldUniqueness(driverDTO);
                    return driverRepository.save(existingDriver);
                })
                .map(driverMapper::mapToDTO);
    }

    public void deleteDriver(Integer id) {
        driverRepository.deleteById(id);
    }

    private void checkFieldUniqueness(DriverDTO driverDTO) {
        Map<Predicate<DriverDTO>, String> checks = Map.of(
                driver -> driverRepository.existsDriverByLicenseNumber(driver.licenseNumber()), "License Number already exists",
                driver -> driverRepository.existsDriverByVehicleNumber(driver.vehicleNumber()), "Vehicle Number already exists",
                driver -> driverRepository.existsDriverByEmail(driver.email()), "Email already exists"
        );

        checks.entrySet().stream()
                .filter(entry -> entry.getKey().test(driverDTO))
                .findFirst()
                .ifPresent(entry -> {
                    throw new UniqueException(entry.getValue());
                });
    }
}
