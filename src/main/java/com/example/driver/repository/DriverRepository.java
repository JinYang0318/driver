package com.example.driver.repository;

import com.example.driver.model.Driver;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends ListCrudRepository<Driver, Integer> {
    Optional<Driver> findById(Integer id);
    boolean existsDriverByLicenseNumber(String licenseNumber);
    boolean existsDriverByEmail(String email);
    boolean existsDriverByVehicleNumber(String vehicleNumber);
}
