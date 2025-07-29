package com.example.blood_donation.controller;

import com.example.blood_donation.dto.HospitalDTO;
import com.example.blood_donation.service.HospitalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
@SecurityRequirement(name = "api")
public class HospitalAPI {
    @Autowired
    private HospitalService hospitalService;
    @GetMapping
    public List<HospitalDTO> getAll() {
        return hospitalService.getAll();
    }

    @GetMapping("/{id}")
    public HospitalDTO getById(@PathVariable Long id) {
        return hospitalService.getById(id);
    }
    @PostMapping
    public HospitalDTO create(@RequestBody HospitalDTO dto) {
        return hospitalService.create(dto);
    }
    @PutMapping("/{id}")
    public HospitalDTO update(@PathVariable Long id, @RequestBody HospitalDTO dto) {
        return hospitalService.update(id, dto);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hospitalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
