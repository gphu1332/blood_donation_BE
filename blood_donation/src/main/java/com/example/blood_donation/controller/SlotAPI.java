package com.example.blood_donation.controller;


import com.example.blood_donation.dto.SlotRequest;
import com.example.blood_donation.dto.SlotResponse;
import com.example.blood_donation.service.SlotService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@SecurityRequirement(name = "api")
//@PreAuthorize("hasRole('ADMIN')")
public class SlotAPI {

    @Autowired
    private SlotService slotService;

    @PostMapping
    public ResponseEntity<SlotResponse> createSlot(@RequestBody SlotRequest request) {
        SlotResponse response = slotService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SlotResponse> getSlotById(@PathVariable Long id) {
        SlotResponse slot = slotService.getSlotById(id);
        return ResponseEntity.ok(slot);
    }
    @GetMapping
    public ResponseEntity<List<SlotResponse>> getAllSlots() {
        List<SlotResponse> slots = slotService.getAll();
        return ResponseEntity.ok(slots);
    }

}
