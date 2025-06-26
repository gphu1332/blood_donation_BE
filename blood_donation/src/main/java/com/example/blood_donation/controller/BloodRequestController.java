package com.example.blood_donation.controller;

import com.example.blood_donation.dto.BloodRequestDTO;
import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.service.BloodRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class BloodRequestController {

    @Autowired
    private BloodRequestService service;

    @PostMapping("/medical")
    public ResponseEntity<?> create(@RequestBody BloodRequestDTO dto) {
        return new ResponseEntity<>(service.createRequest(dto), HttpStatus.CREATED);
    }

    @PutMapping("/medical/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BloodRequestDTO dto) {
        return ResponseEntity.ok(service.updateRequest(id, dto));
    }

    @PutMapping("/staff/{id}/respond")
    public ResponseEntity<?> respond(
            @PathVariable Long id,
            @RequestBody String action,
            @RequestParam Long staffId
    ) {
        return ResponseEntity.ok(service.respondToRequest(id, action, staffId));
    }

    @PutMapping("/staff/{id}/process")
    public ResponseEntity<?> process(
            @PathVariable Long id,
            @RequestParam Status status
    ) {
        return ResponseEntity.ok(service.updateProcessingStatus(id, status));
    }

    @GetMapping("/medical/{medId}")
    public List<BloodRequest> getByMedical(@PathVariable Long medId) {
        return service.getRequestByMedical(medId);
    }

    @GetMapping("/staff/{staId}")
    public List<BloodRequest> getByStaff(@PathVariable Long staId) {
        return service.getRequestsByStaff(staId);
    }

    @GetMapping
    public List<BloodRequest> getAll() {
        return service.getAllRequests();
    }
}
