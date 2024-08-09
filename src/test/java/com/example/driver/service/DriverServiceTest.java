package com.example.driver.service;

import com.example.driver.dto.DriverDTO;
import com.example.driver.exception.UniqueException;
import com.example.driver.mapper.DriverMapper;
import com.example.driver.mapper.Mapper;
import com.example.driver.mock.MockDriver;
import com.example.driver.model.Driver;
import com.example.driver.repository.DriverRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DriverService.class, DriverMapper.class})
class DriverServiceTest {
    @Autowired
    private DriverService driverService;

    @MockBean
    private DriverRepository driverRepository;

    @SpyBean
    private Mapper<Driver, DriverDTO> driverMapper;

    private static Stream<Arguments> checkCreateFieldUniqueness() {
        return Stream.of(
                Arguments.of("License Number exists", true, false, false, "License Number already exists"),
                Arguments.of("Vehicle Number exists", false, true, false, "Vehicle Number already exists"),
                Arguments.of("Email exists", false, false, true, "Email already exists")
        );
    }

    @Test
    @DisplayName("Given: driver id found, When: getDriverById, Then: return driverDTO")
    void getDriverById() {
        Optional<DriverDTO> expectedDriverDTO = Optional.of(
                MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123"));
        when(driverRepository.findById(anyInt()))
                .thenReturn(Optional.of(MockDriver.getDriver(1)));

        Optional<DriverDTO> resultDriverDTO = driverService.getDriverById(1);

        verify(driverRepository).findById(1);
        verify(driverMapper).mapToDTO(any(Driver.class));
        assertThat(resultDriverDTO).isEqualTo(expectedDriverDTO);
    }

    @Test
    @DisplayName("Given: driver id not found, When: getDriverById, Then: return optional empty")
    void getDriverByIdNotFound() {
        when(driverRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Optional<DriverDTO> resultDriverDTO = driverService.getDriverById(9999);

        verify(driverRepository).findById(9999);
        verify(driverMapper, never()).mapToDTO(any(Driver.class));
        assertThat(resultDriverDTO).isEmpty();
    }

    @Test
    @DisplayName("Given: -, When: getAllDrivers, Then: return driverDTO")
    void getAllDrivers() {
        List<DriverDTO> driverDTOList = List.of(
                MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123"));
        when(driverRepository.findAll()).thenReturn(List.of(MockDriver.getDriver(1)));

        List<DriverDTO> resultDriverDTOList = driverService.getAllDrivers();

        verify(driverRepository).findAll();
        verify(driverMapper).mapToDTO(any(Driver.class));
        assertThat(resultDriverDTOList).isEqualTo(driverDTOList);
    }

    @Test
    @DisplayName("Given: driverIds, When: getDriverByIds, Then: return driverDTO List")
    void getDriverByIds() {
        List<DriverDTO> driverDTOList = List.of(
                MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123"));
        when(driverRepository.findAllById(anyList())).thenReturn(List.of(MockDriver.getDriver(1)));

        List<DriverDTO> resultDriverDTOList = driverService.getDriverByIds(List.of(1));

        verify(driverRepository).findAllById(List.of(1));
        verify(driverMapper).mapToDTO(any(Driver.class));
        assertThat(resultDriverDTOList).isEqualTo(driverDTOList);
    }

    @Test
    @DisplayName("Given: driverIds not found, When: getDriverByIds, Then: return empty driverDTO List")
    void getDriverByIdsNotFound() {
        when(driverRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        List<DriverDTO> resultDriverDTOList = driverService.getDriverByIds(List.of(9999));

        verify(driverRepository).findAllById(List.of(9999));
        verify(driverMapper, never()).mapToDTO(any(Driver.class));
        assertThat(resultDriverDTOList).isEmpty();
    }

    @Test
    @DisplayName("Given: driver, When: createDriver, Then: return driverDTO")
    void createDriver() {
        Optional<DriverDTO> expectedDriverDTO =
                Optional.of(MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123"));
        when(driverRepository.save(any(Driver.class))).thenReturn(MockDriver.getDriver(1));

        Optional<DriverDTO> resultDriverDTO = driverService.createDriver(expectedDriverDTO.get());
        verify(driverRepository).save(any());
        verify(driverMapper).mapToDTO(any(Driver.class));
        assertThat(resultDriverDTO).isEqualTo(expectedDriverDTO);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("checkCreateFieldUniqueness")
    void createDriverThrowException(String scenario, boolean licenseExists, boolean vehicleExists, boolean emailExists, String expectedMessage) {
        DriverDTO driverDTO =
                MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123");

        when(driverRepository.existsDriverByLicenseNumber(driverDTO.licenseNumber())).thenReturn(licenseExists);
        when(driverRepository.existsDriverByVehicleNumber(driverDTO.vehicleNumber())).thenReturn(vehicleExists);
        when(driverRepository.existsDriverByEmail(driverDTO.email())).thenReturn(emailExists);

        UniqueException thrown = assertThrows(UniqueException.class, () -> {
            driverService.createDriver(driverDTO);
        });

        assertEquals(expectedMessage, thrown.getMessage());
    }

    @Test
    @DisplayName("Given: driverId, When: delete, Then: success delete")
    void deleteDriver() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(MockDriver.getDriver(1)));
        driverService.deleteDriver(1);

        verify(driverRepository).deleteById(1);
    }
}
