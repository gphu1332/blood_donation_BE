package com.example.blood_donation.controller;

import com.example.blood_donation.dto.BloodUnitResponseDTO;
import com.example.blood_donation.dto.CreateBloodUnitDTO;
import com.example.blood_donation.dto.UpdateBloodUnitDTO;
import com.example.blood_donation.entity.BloodUnit;
import com.example.blood_donation.service.BloodUnitService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-units")
@SecurityRequirement(name = "api")
public class BloodUnitController {
    @Autowired
    private BloodUnitService service;

    @GetMapping
    public ResponseEntity<List<BloodUnitResponseDTO>> getAll() {
        List <BloodUnitResponseDTO> list = service.getAll().stream()
                .map(service::toResponseDTO)
                .toList();
        return ResponseEntity.ok(list);
    }
    @GetMapping("/{id}")
    public ResponseEntity<BloodUnitResponseDTO> getById(@PathVariable Long id) {
        BloodUnit unit = service.getById(id);
        return ResponseEntity.ok(service.toResponseDTO(unit));
    }
    @PostMapping
    public ResponseEntity<BloodUnitResponseDTO> create(@RequestBody CreateBloodUnitDTO dto) {
        BloodUnit unit = service.create(dto);
        return new ResponseEntity<>(service.toResponseDTO(unit), HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<BloodUnitResponseDTO> update(@PathVariable Long id, @RequestBody UpdateBloodUnitDTO dto) {
        BloodUnit updated = service.update(id, dto);
        return ResponseEntity.ok(service.toResponseDTO(updated));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
