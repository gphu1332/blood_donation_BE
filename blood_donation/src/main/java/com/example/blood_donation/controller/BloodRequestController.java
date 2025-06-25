package com.example.blood_donation.controller;

import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.service.BloodRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/blood-requests")
public class BloodRequestController {

    @Autowired
    private BloodRequestService service;

    //1. Lấy danh sách yêu cầu cần máu đang chờ xử lý
    @GetMapping
    public ResponseEntity<?> getRequestsForStaff() {
        return ResponseEntity.ok(service.getAllPending());
    }
    //2. Cập nhập trạng thái yêu cầu
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String status) {
        service.updateStatus(id, Status.valueOf(status.toUpperCase()));
        return ResponseEntity.ok("Status updated");
    }
    //3. Gán ngươời xử lý yêu cầu
    @PatchMapping("/{id}/assign")
    public ResponseEntity<?> assignStaff(@PathVariable Long id, @RequestParam Long staffId) {
        service.assignStaff(id, staffId);
        return ResponseEntity.ok("Staff assigned");
    }
    /*
    @GetMapping
    public List<BloodRequest> getAll() {
        return service.getAll();
    }
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
    }*/
}
