package com.example.driver.controller;

import com.example.driver.dto.DriverDTO;
import com.example.driver.exception.NotFoundException;
import com.example.driver.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {
    private final DriverService driverService;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverDTO> getDriverById(@PathVariable("id") Integer id) {
        DriverDTO driverDTO = driverService.getDriverById(id)
                .orElseThrow(() -> new NotFoundException("Driver with ID " + id + " not found"));
        return ResponseEntity.ok().body(driverDTO);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        List<DriverDTO> driverDTOs = driverService.getAllDrivers();
        return ResponseEntity.ok().body(driverDTOs);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DriverDTO>> getDriverByIds(@RequestParam(value = "id", required = false) List<Integer> ids) {
        List<DriverDTO> driverDTOList = driverService.getDriverByIds(ids);
        List<Integer> returnedIds = driverDTOList
                .stream()
                .map(DriverDTO::id)
                .toList();

        List<Integer> missingIds = ids.stream()
                .filter(id -> !returnedIds.contains(id))
                .toList();

        HttpHeaders headers = new HttpHeaders();
        if (!missingIds.isEmpty()) {
            headers.add("X-MISSING-SET", missingIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
        }

        log.info("X-MISSING-SET {}", missingIds);
        return ResponseEntity.ok().headers(headers).body(driverDTOList);
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverDTO> createDriver(@Valid @RequestBody DriverDTO driverDTO) {
        return driverService.createDriver(driverDTO)
                .map(driver -> ResponseEntity.status(HttpStatus.CREATED).body(driver))
                .orElse(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverDTO> updateDriver(@PathVariable("id") Integer id, @Valid @RequestBody DriverDTO driverDTO) {
        return driverService.updateDriver(id, driverDTO)
                .map(driver -> ResponseEntity.status(HttpStatus.OK).body(driver))
                .orElseThrow(() -> new NotFoundException("Driver with ID " + id + " not found"));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteDriver(@PathVariable("id") Integer id) {
        DriverDTO driverDTO = driverService.getDriverById(id)
                .orElseThrow(() -> new NotFoundException("Driver with ID " + id + " not found"));

        driverService.deleteDriver(id);

        return ResponseEntity.status(HttpStatus.OK).body("Driver with ID " + id + " successfully deleted.");
    }
}
