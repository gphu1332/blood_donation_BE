package com.example.blood_donation.controller;

import com.example.blood_donation.dto.DonationProgramDTO;
import com.example.blood_donation.service.DonationProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programs")
public class DonationProgramAPI {

    @Autowired
    private DonationProgramService service;

    @GetMapping
    public List<DonationProgramDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonationProgramDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<DonationProgramDTO> create(@RequestBody DonationProgramDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DonationProgramDTO> update(@PathVariable Long id, @RequestBody DonationProgramDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}