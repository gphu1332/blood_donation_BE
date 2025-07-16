package com.example.blood_donation.controller;

import com.example.blood_donation.dto.HealthCheckRequest;
import com.example.blood_donation.entity.HealthCheck;
import com.example.blood_donation.service.HealthCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-check")
@SecurityRequirement(name = "api")
@Tag(name = "Health Check API", description = "Quản lý kiểm tra sức khỏe trước khi hiến máu")
public class HealthCheckAPI {
    @Autowired
    HealthCheckService healthCheckService;

    @Operation(
            summary = "Tạo mới thông tin kiểm tra sức khỏe",
            description = "Tạo bản ghi kiểm tra sức khỏe cho một chương trình hiến máu cụ thể"
    )
    @ApiResponse(responseCode = "200", description = "Tạo thành công")
    @PostMapping
    public ResponseEntity<HealthCheck> createHealthCheck(@Valid @RequestBody HealthCheckRequest request) {
        HealthCheck created = healthCheckService.createHealthCheckFromRequest(request);
        return ResponseEntity.ok(created);
    }

    @Operation(
            summary = "Cập nhật thông tin kiểm tra sức khỏe",
            description = "Cập nhật bản ghi kiểm tra sức khỏe dựa trên ID"
    )
    @ApiResponse(responseCode = "200", description = "Cập nhật thành công")
    @PutMapping("/{id}")
    public ResponseEntity updateHealthCheck(
            @PathVariable Long id,
            @Valid @RequestBody HealthCheckRequest request) {
        HealthCheck updated = healthCheckService.updateHealthCheckFromRequest(id, request);
        return ResponseEntity.ok(updated);
    }
}
