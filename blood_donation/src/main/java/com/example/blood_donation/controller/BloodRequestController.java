package com.example.blood_donation.controller;

import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.service.BloodRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/blood-requests")
public class BloodRequestController {

    @Autowired
    private BloodRequestService service;

    @GetMapping
    public List<BloodRequest> getAll() {
        return service.getAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<BloodRequest> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<BloodRequest> update(@PathVariable Long id, @RequestBody BloodRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
