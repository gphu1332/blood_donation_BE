package com.example.blood_donation.controller;

import com.example.blood_donation.dto.BloodRequestDTO;
import com.example.blood_donation.dto.BloodRequestResponseDTO;
import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.service.BloodRequestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@SecurityRequirement(name = "api")
public class BloodRequestAPI{

    @Autowired
    private BloodRequestService service;

    @PostMapping("/hospital")
    public ResponseEntity<BloodRequestResponseDTO> create(@RequestBody BloodRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertToResponse(service.createRequestFromDTO(dto)));
    }

    @PutMapping("/hospital/{id}")
    public ResponseEntity<BloodRequestResponseDTO> update(@PathVariable Long id, @RequestBody BloodRequestDTO dto) {
        return ResponseEntity.ok(convertToResponse(service.updateRequestByMedical(id, dto)));
    }

    @PutMapping("/hospital/{id}/cancel")
    public ResponseEntity<String> cancel(@PathVariable Long id) {
        try {
            service.cancelRequestByMedical(id);
            return ResponseEntity.ok("Yêu cầu đã được hủy thành công.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/hospital/{medId}")
    public ResponseEntity<List<BloodRequestResponseDTO>> getByHospital(@PathVariable Long medId) {
        return ResponseEntity.ok(service.getRequestDTOByMedical(medId));
    }

    @PutMapping("/staff/{id}/respond")
    public ResponseEntity<BloodRequestResponseDTO> respond(
            @PathVariable Long id,
            @RequestParam String action,
            @RequestParam Long staffId
    ) {
        return ResponseEntity.ok(convertToResponse(service.respondToRequest(id, action, staffId)));
    }

    @PutMapping("/staff/{id}/process")
    public ResponseEntity<BloodRequestResponseDTO> process(
            @PathVariable Long id,
            @RequestParam Status status
    ) {
        return ResponseEntity.ok(convertToResponse(service.updateProcessingStatus(id, status)));
    }


    @GetMapping("/staff/{staId}")
    public ResponseEntity<List<BloodRequestResponseDTO>> getByStaff(@PathVariable Long staId) {
        return ResponseEntity.ok(
                service.getRequestsByStaff(staId).stream().map(this::convertToResponse).toList()
        );
    }

    //Kim API test
    @GetMapping("/kimrequests")
    public ResponseEntity<List<BloodRequestResponseDTO>> getAllRequests() {
        return ResponseEntity.ok(service.getAllRequestDTOs());
    }

    /*@GetMapping("/kimrequests/{medId}")
    public ResponseEntity<List<BloodRequestResponseDTO>> getCleanRequestsByHospital(@PathVariable Long medId) {
        return ResponseEntity.ok(service.getRequestsByMedicalDTO(medId));
    }*/

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private BloodRequestResponseDTO convertToResponse(BloodRequest req) {
        return service.getAllRequestDTOs().stream()
                .filter(r -> r.getReqID().equals(req.getReqID()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không thể chuyển đổi sang DTO"));
    }
}

