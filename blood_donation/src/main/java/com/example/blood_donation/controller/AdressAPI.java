package com.example.blood_donation.controller;

import com.example.blood_donation.dto.AdressDTO;
import com.example.blood_donation.service.AdressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@SecurityRequirement(name = "api")
@Tag(name = "Address API")
public class AdressAPI {

    @Autowired
    private AdressService adressService;

    @GetMapping
    public ResponseEntity<List<AdressDTO>> getAll() {
        return ResponseEntity.ok(adressService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdressDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(adressService.getById(id));
    }

    @PostMapping
    public ResponseEntity<AdressDTO> create(@RequestBody AdressDTO dto) {
        return ResponseEntity.ok(adressService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdressDTO> update(@PathVariable Long id, @RequestBody AdressDTO dto) {
        return ResponseEntity.ok(adressService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
