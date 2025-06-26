package com.example.blood_donation.controller;

import com.example.blood_donation.dto.BloodRequestDTO;
import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.service.BloodRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
<<<<<<< HEAD
@RequestMapping("/api/requests")
=======
@RequestMapping("/api/blood-requests")
>>>>>>> main
public class BloodRequestController {
    @Autowired
    private BloodRequestService service;
<<<<<<< HEAD

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

=======
>>>>>>> main
    @GetMapping
    public List<BloodRequest> getAll() {
        return service.getAllRequests();
    }
<<<<<<< HEAD
=======
    @GetMapping("/{id}")
    public ResponseEntity<BloodRequest> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }
    @PostMapping
    public ResponseEntity<BloodRequest> create(@RequestBody BloodRequest req) {
        return new ResponseEntity<>(service.create(req), HttpStatus.CREATED);
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
>>>>>>> main
}
