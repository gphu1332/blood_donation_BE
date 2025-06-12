package com.example.blood_donation.controller;

import com.example.blood_donation.entity.BloodInventory;
import com.example.blood_donation.service.BloodInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-inventory")
public class BloodInventoryAPI {
    @Autowired
    private BloodInventoryService service;

    @GetMapping
    public List<BloodInventory> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BloodInventory> getById(@PathVariable Integer id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BloodInventory> create(@RequestBody BloodInventory bloodInventory) {
        BloodInventory saved = service.save(bloodInventory);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BloodInventory> update(@PathVariable Integer id, @RequestBody BloodInventory bloodInventory) {
        if (!service.getById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        bloodInventory.setBloodInvID(id);
        BloodInventory updated = service.save(bloodInventory);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!service.getById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
