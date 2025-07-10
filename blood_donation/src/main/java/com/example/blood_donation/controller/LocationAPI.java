package com.example.blood_donation.controller;

import com.example.blood_donation.dto.LocationDTO;
import com.example.blood_donation.service.CityService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')")
public class LocationAPI {

    @Autowired
    private CityService cityService;

    @GetMapping
    public List<LocationDTO> getAllLocations() {
        return cityService.getAllLocations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocation(@PathVariable Long id) {
        return ResponseEntity.ok(cityService.getLocationById(id));
    }

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO dto) {
        return ResponseEntity.ok(cityService.createLocation(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(@PathVariable Long id, @RequestBody LocationDTO dto) {
        return ResponseEntity.ok(cityService.updateLocation(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Long id) {
        cityService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
