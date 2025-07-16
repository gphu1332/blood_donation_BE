package com.example.blood_donation.controller;

import com.example.blood_donation.dto.BloodRequestDTO;
import com.example.blood_donation.dto.BloodRequestResponseDTO;
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
public class BloodRequestAPI{

    @Autowired
    private BloodRequestService service;

    @PostMapping("/hospital")
    public ResponseEntity<?> create(@RequestBody BloodRequestDTO dto) {
        return new ResponseEntity<>(service.createRequest(dto), HttpStatus.CREATED);
    }

    @PutMapping("/hospital/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BloodRequestDTO dto) {
        return ResponseEntity.ok(service.updateRequest(id, dto));
    }

    @PutMapping("/hospital/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        service.cancelRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/staff/{id}/respond")
    public ResponseEntity<?> respond(
            @PathVariable Long id,
            @RequestParam String action,
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

    @GetMapping("/hospital/{medId}")
    public List<BloodRequest> getByHospital(@PathVariable Long medId) {
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

    //Kim API test
    @GetMapping("/kimrequests")
    public ResponseEntity<List<BloodRequestResponseDTO>> getAllRequests() {
        return ResponseEntity.ok(service.getAllRequestDTOs());
    }

    @GetMapping("/kimrequests/{medId}")
    public ResponseEntity<List<BloodRequestResponseDTO>> getCleanRequestsByHospital(@PathVariable Long medId) {
        return ResponseEntity.ok(service.getRequestsByMedicalDTO(medId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable Long id) {
        service.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<?> cancelRequest(@PathVariable Long id) {
        try {
            service.cancelRequest(id);
            return ResponseEntity.ok("Yêu cầu đã được hủy");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}

