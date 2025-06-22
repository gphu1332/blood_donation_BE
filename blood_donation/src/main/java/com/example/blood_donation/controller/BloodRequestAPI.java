package com.example.blood_donation.controller;

import com.example.blood_donation.dto.BloodRequestDTO;
import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.entity.BloodRequestPriority;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.service.BloodRequestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-requests")
@SecurityRequirement(name = "api")
public class BloodRequestAPI {
    @Autowired
    private BloodRequestService service;
    @PostMapping
    public ResponseEntity<BloodRequest> create(@RequestBody BloodRequestDTO wrapper) {
        BloodRequest saved = service.create(wrapper.getRequest(), wrapper.getPriorities());
        return ResponseEntity.ok(saved);
    }
    @GetMapping
    public List<BloodRequest> getAll() {
        return service.getAll();
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<BloodRequest> updateStatus(@PathVariable Integer id, @RequestParam Status status) {
        return ResponseEntity.ok(service.updateStatus(id,status));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/priorities")
    public List<BloodRequestPriority> getPriorities(@PathVariable Integer id) {
        return service.getPriorityList(id);
    }
}
