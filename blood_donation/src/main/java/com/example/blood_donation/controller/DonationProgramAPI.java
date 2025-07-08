package com.example.blood_donation.controller;

import com.example.blood_donation.dto.DonationProgramDTO;
import com.example.blood_donation.service.DonationProgramService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/programs")
@SecurityRequirement(name = "api")
public class DonationProgramAPI {

    @Autowired
    private DonationProgramService service;

    @GetMapping
    public List<DonationProgramDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonationProgramDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Tạo chương trình hiến máu mới.
     * Gán admin dựa trên tài khoản đang đăng nhập.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DonationProgramDTO> create(
            @RequestBody DonationProgramDTO dto,
            Principal principal
    ) {
        DonationProgramDTO created = service.create(dto, principal.getName());
        return ResponseEntity.ok(created);
    }

    /**
     * Cập nhật chương trình.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DonationProgramDTO> update(
            @PathVariable Long id,
            @RequestBody DonationProgramDTO dto
    ) {
        DonationProgramDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Xóa chương trình.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Xóa chương trình thành công!");
    }

    /**
     * Tìm kiếm chương trình theo ngày giữa starDate và endDate và địa điểm.
     */
    @GetMapping("/search")
    public ResponseEntity<List<DonationProgramDTO>> searchByDateRangeAndLocation(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("locationId") Long locationId
    ) {
        List<DonationProgramDTO> results = service.searchByDateInRangeAndLocationID(date, locationId);
        return ResponseEntity.ok(results);
    }
    @GetMapping("/search-range")
    public ResponseEntity<List<DonationProgramDTO>> searchByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<DonationProgramDTO> results = service.searchByDateRange(startDate, endDate);
        return ResponseEntity.ok(results);
    }


}
