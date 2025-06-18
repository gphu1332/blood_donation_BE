package com.example.blood_donation.controller;


import com.example.blood_donation.dto.RegisterSlotDTO;
import com.example.blood_donation.entity.Slot;
import com.example.blood_donation.entity.Appointment;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.service.SlotService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@SecurityRequirement(name = "api")
public class SlotAPI {

    @Autowired
    private SlotService slotService;

    @GetMapping
    public ResponseEntity<List<Slot>> getAllSlots() {
        return ResponseEntity.ok(slotService.getSlot());
    }

    @PostMapping
    public void generateSlot() {
        slotService.generateSlot();
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerSlot(@RequestBody RegisterSlotDTO registerSlotDTO) {
        return ResponseEntity.ok(slotService.registerSlot(registerSlotDTO));
    }

}
