package com.example.blood_donation.controller;

import com.example.blood_donation.dto.CreateDonationDetailDTO;
import com.example.blood_donation.dto.DonationDetailDTO;
import com.example.blood_donation.service.DonationDetailService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donation-details")
@SecurityRequirement(name = "api")
public class DonationDetailAPI {
    @Autowired
    DonationDetailService detailService;
    @PostMapping
    public ResponseEntity<DonationDetailDTO> create (@RequestBody CreateDonationDetailDTO dto) {
        return ResponseEntity.ok(detailService.create(dto));
    }
    @GetMapping("/{id}")
    public ResponseEntity<DonationDetailDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(detailService.getById(id));
    }
    @GetMapping
    public ResponseEntity<List<DonationDetailDTO>> getAll() {
        return ResponseEntity.ok(detailService.getAll());
    }
    @PutMapping("/{id}")
    public ResponseEntity<DonationDetailDTO> update(@PathVariable Long id, @RequestBody CreateDonationDetailDTO dto) {
        return ResponseEntity.ok(detailService.update(id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        detailService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
