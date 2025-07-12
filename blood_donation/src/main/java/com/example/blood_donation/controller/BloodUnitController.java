package com.example.blood_donation.controller;

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
    public List<BloodUnit> getAll() {
        return service.getAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<BloodUnit> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    @PostMapping
    public ResponseEntity<BloodUnit> create(@RequestBody BloodUnit unit) {
        return new ResponseEntity<>(service.create(unit), HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<BloodUnit> update(@PathVariable Long id, @RequestBody BloodUnit unit) {
        return ResponseEntity.ok(service.update(id, unit));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
