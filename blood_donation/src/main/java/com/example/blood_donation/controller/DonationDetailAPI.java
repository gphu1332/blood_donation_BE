package com.example.blood_donation.controller;

import com.example.blood_donation.dto.CreateDonationDetailDTO;
import com.example.blood_donation.dto.DonationDetailDTO;
import com.example.blood_donation.service.DonationDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donation-details")
@SecurityRequirement(name = "api")
public class DonationDetailAPI {

    @Autowired
    private DonationDetailService detailService;

    @PostMapping
    @Operation(summary = "Tạo thông tin hiến máu")
    public ResponseEntity<?> create(@RequestBody CreateDonationDetailDTO dto) {
        return handle(() -> detailService.create(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin hiến máu theo ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return handle(() -> detailService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Lấy tất cả thông tin hiến máu")
    public ResponseEntity<List<DonationDetailDTO>> getAll() {
        return ResponseEntity.ok(detailService.getAll());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin hiến máu")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CreateDonationDetailDTO dto) {
        return handle(() -> detailService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xoá thông tin hiến máu")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        detailService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-appointment/{appointmentId}")
    @Operation(summary = "Lấy thông tin hiến máu theo lịch hẹn")
    public ResponseEntity<?> getByAppointmentId(@PathVariable Long appointmentId) {
        return handle(() -> detailService.getByAppointmentId(appointmentId));
    }

    /**
     * Xử lý try-catch dùng functional interface để gom lỗi
     */
    private ResponseEntity<?> handle(ThrowingSupplier supplier) {
        try {
            return ResponseEntity.ok(supplier.get());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

    @FunctionalInterface
    private interface ThrowingSupplier {
        Object get() throws Exception;
    }
}
