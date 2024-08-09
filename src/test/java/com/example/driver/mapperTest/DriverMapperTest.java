package com.example.driver.mapperTest;

import com.example.driver.dto.DriverDTO;
import com.example.driver.mapper.DriverMapper;
import com.example.driver.mock.MockDriver;
import com.example.driver.model.Driver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DriverMapperTest {
    private final DriverMapper driverMapper = new DriverMapper();

    @Test
    @DisplayName("Given: DriverDTO, When: mapToEntity, Then: return Driver")
    void mapToEntity(){
        Driver driver = MockDriver.getDriver();
        DriverDTO driverDTO = MockDriver.getDriverDTO();

        assertThat(driverMapper.mapToEntity(driverDTO)).isEqualTo(driver);
    }

    @Test
    @DisplayName("Given: Driver, When: mapToDTO, Then: return DriverDTO")
    void mapToDTO(){
        Driver driver = MockDriver.getDriver();
        DriverDTO driverDTO = MockDriver.getDriverDTO();

        assertThat(driverMapper.mapToDTO(driver)).isEqualTo(driverDTO);
    }
}
