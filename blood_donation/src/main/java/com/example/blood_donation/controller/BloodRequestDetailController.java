package com.example.blood_donation.controller;

import com.example.blood_donation.entity.BloodRequestDetail;
import com.example.blood_donation.entity.BloodRequestDetailId;
import com.example.blood_donation.service.BloodRequestDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-request-details")
public class BloodRequestDetailController {
    @Autowired
    private BloodRequestDetailService service;
    @GetMapping
    public List<BloodRequestDetail> getAll() {
        return service.getAll();
    }
    @GetMapping("/{reqID}/{bloodType}")
    public ResponseEntity<BloodRequestDetail> getById(@PathVariable Long reqID, @PathVariable String bloodType) {
        return ResponseEntity.ok(service.getById(reqID, bloodType));
    }
    @PostMapping
    public ResponseEntity<BloodRequestDetail> create(@RequestBody BloodRequestDetail detail) {
        return new ResponseEntity<>(service.create(detail), HttpStatus.CREATED);
    }
    @PutMapping("/{reqID}/{bloodType}")
    public ResponseEntity<BloodRequestDetail> update (@PathVariable Long reqID,
                                                      @PathVariable String bloodType,
                                                      @RequestBody BloodRequestDetail detail) {
        return ResponseEntity.ok(service.update(reqID, bloodType, detail));
    }
    @DeleteMapping("/{reqID}/{bloodType}")
    public ResponseEntity<Void> delete(@PathVariable Long reqID, @PathVariable String bloodType) {
        service.delete(reqID, bloodType);
        return ResponseEntity.noContent().build();
    }
}
