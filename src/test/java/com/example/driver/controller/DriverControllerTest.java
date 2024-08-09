package com.example.driver.controller;

import com.example.driver.dto.DriverDTO;
import com.example.driver.exception.UniqueException;
import com.example.driver.mock.MockDriver;
import com.example.driver.service.DriverService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriverController.class)
class DriverControllerTest {
    private final static String DRIVER_URL = "/api/driver";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DriverService driverService;

    private static Stream<Arguments> invalidParam() {
        return Stream.of(
                Arguments.of("blank request param", "?id= "),
                Arguments.of("empty request param", "?id=")
        );
    }

    private static Stream<Arguments> emptyCreateValue() {
        return Stream.of(
                Arguments.of("empty name but other field have value", MockDriver.getDriverDTO(1, "", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123"), "{\"name\":\"Name is required\"}"),
                Arguments.of("empty email but other field have value", MockDriver.getDriverDTO(1, "Robert Brown", "", "D1122334", "Ford Focus", "LMN9123"), "{\"email\":\"Email is required\"}"),
                Arguments.of("empty license number but other field have value", MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "", "Ford Focus", "LMN9123"), "{\"licenseNumber\":\"License Number is required\"}"),
                Arguments.of("empty vehicle model but other field have value", MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "D1122334", "", "LMN9123"), "{\"vehicleModel\":\"Vehicle Model is required\"}"),
                Arguments.of("empty vehicle number but other field have value", MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "", "Ford Focus", ""), "{\"vehicleNumber\":\"Vehicle Number is required\"}"),
                Arguments.of("empty all field", MockDriver.getDriverDTO(1, "", "", "", "", ""), "{\"name\":\"Name is required\",\"vehicleNumber\":\"Vehicle Number is required\",\"vehicleModel\":\"Vehicle Model is required\",\"licenseNumber\":\"License Number is required\",\"email\":\"Email is required\"}")
        );
    }

    private static Stream<Arguments> checkCreateFieldUniqueness() {
        return Stream.of(
                Arguments.of("License Number exists", true, false, false, "License Number already exists"),
                Arguments.of("Vehicle Number exists", false, true, false, "Vehicle Number already exists"),
                Arguments.of("Email exists", false, false, true, "Email already exists")
        );
    }

    @Test
    @DisplayName("Given: driverId found, When: GET /api/driver/1, Then: return 200 status with driverDTO")
    void getDriverById() throws Exception {
        DriverDTO driverDTO = MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123");
        when(driverService.getDriverById(anyInt()))
                .thenReturn(Optional.of(driverDTO));

        mockMvc.perform(get(DRIVER_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(driverDTO)));

        verify(driverService).getDriverById(1);
    }

    @Test
    @DisplayName("Given: invalid driverId, When: GET /api/driver/1a, Then: return 400 status with Bad Request")
    void getDriverByInvalidDriverId() throws Exception {
        mockMvc.perform(get(DRIVER_URL + "/1a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Failed to convert 'id' with value: '1a'"));

        verifyNoInteractions(driverService);
    }

    @Test
    @DisplayName("Given: driverId not found, When: GET /api/driver/9999, Then: return 404 status with Not Found")
    void getDriverByDriverIdNotFound() throws Exception {
        mockMvc.perform(get(DRIVER_URL + "/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Driver with ID 9999 not found"));

        verify(driverService).getDriverById(9999);
    }

    @Test
    @DisplayName("Given: -, When: GET /api/driver/all, Then: return 200 status driverDTO list")
    void getAllDrivers() throws Exception {
        List<DriverDTO> driverDTOList =
                List.of(MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123"));
        when(driverService.getAllDrivers())
                .thenReturn(driverDTOList);

        mockMvc.perform(get(DRIVER_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(driverDTOList)));

        verify(driverService).getAllDrivers();
    }

    @Test
    @DisplayName("Given: valid driverIds, When: GET /api/driver?id=1, Then: return 200 status DriverDTO list")
    void getDriverByIds() throws Exception {
        List<DriverDTO> driverDTOList =
                List.of(MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123"));
        when(driverService.getDriverByIds(anyList()))
                .thenReturn(driverDTOList);

        mockMvc.perform(get(DRIVER_URL + "?id=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(driverDTOList)));

        verify(driverService).getDriverByIds(List.of(1));
    }

    @Test
    @DisplayName("Given: driverIds not found, When: GET /api/driver?id=999, Then: return 200 with empty list")
    void getDriverByIdsNotFound() throws Exception {
        mockMvc.perform(get(DRIVER_URL + "?id=999"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andExpect(header().string("X-MISSING-SET", "999"));

        verify(driverService).getDriverByIds(List.of(999));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidParam")
    void getDriverByInvalidIds(String scenario, String requestParam) throws Exception {
        mockMvc.perform(get(DRIVER_URL + requestParam))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(driverService).getDriverByIds(anyList());
    }

    @Test
    @DisplayName("Given: driverIds one found and one not found, When: GET /api/driver?id=1&id=999, Then: Then: return 200 with header X-MISSING-SET")
    void getDriverByIdsOneFoundAndOneNotFound() throws Exception {
        List<DriverDTO> driverDTOList =
                List.of(MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123"));
        when(driverService.getDriverByIds(anyList()))
                .thenReturn(driverDTOList);

        mockMvc.perform(get(DRIVER_URL + "?id=1&id=999"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(driverDTOList)))
                .andExpect(header().string("X-MISSING-SET", "999"));

        verify(driverService).getDriverByIds(List.of(1, 999));
    }

    @Test
    @DisplayName("Given: driver, When: POST /api/driver , Then: return 201 status created with driverDTO")
    void createDriver() throws Exception {
        DriverDTO driverDTO = MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123");

        when(driverService.createDriver(any(DriverDTO.class))).thenReturn(Optional.of(driverDTO));

        mockMvc.perform(post(DRIVER_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(driverDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(driverDTO)));

        verify(driverService).createDriver(any(DriverDTO.class));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("emptyCreateValue")
    void createDriverEmptyValue(String scenario, DriverDTO driverDTO, String expectedMessage) throws Exception {
        mockMvc.perform(post(DRIVER_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(driverDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(expectedMessage));

        verify(driverService, never()).createDriver(any(DriverDTO.class));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("checkCreateFieldUniqueness")
    void checkCreateFieldUniqueness(String scenario, boolean licenseExists, boolean vehicleExists, boolean emailExists, String expectedMessage) throws Exception {
        DriverDTO driverDTO =
                MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123");
        when(driverService.createDriver(any(DriverDTO.class)))
                .thenThrow(new UniqueException(expectedMessage));

        mockMvc.perform(post(DRIVER_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(driverDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(expectedMessage));

        verify(driverService).createDriver(any(DriverDTO.class));
    }

    @Test
    @DisplayName("Given: driverId found, When: DELETE /api/driver/1, Then: return 200 status with success message")
    void deleteDriver() throws Exception {
        DriverDTO driverDTO =
                MockDriver.getDriverDTO(1, "Robert Brown", "robert.brown@example.com", "D1122334", "Ford Focus", "LMN9123");
        when(driverService.getDriverById(1)).thenReturn(Optional.of(driverDTO));

        mockMvc.perform(delete(DRIVER_URL + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Driver with ID 1 successfully deleted."));

        verify(driverService).deleteDriver(1);
    }

    @Test
    @DisplayName("Given: invalid driverId , When: DELETE /api/driver/1a, Then: return 400 Bad Request")
    void deleteDriverByInvalidId() throws Exception {
        mockMvc.perform(delete(DRIVER_URL + "/1a"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Failed to convert 'id' with value: '1a'"));

        verifyNoInteractions(driverService);
    }

    @Test
    @DisplayName("Given: driverId not found, When: DELETE /api/driver/9999, Then: return 404 Not Found")
    void deleteDriverByIdNotFound() throws Exception {
        driverService.deleteDriver(9999);
        mockMvc.perform(delete(DRIVER_URL + "/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Driver with ID 9999 not found"));

        verify(driverService).deleteDriver(9999);
    }

}
