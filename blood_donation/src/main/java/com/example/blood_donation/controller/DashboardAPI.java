package com.example.blood_donation.controller;

import com.example.blood_donation.dto.*;
import com.example.blood_donation.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardAPI {

    private final DashboardService dashboardService;

    @GetMapping("/appointments/summary")
    @Operation(summary = "Tổng số đơn hiến máu và tỷ lệ hoàn thành")
    public ResponseEntity<Map<String, Object>> getAppointmentSummary() {
        return ResponseEntity.ok(dashboardService.getAppointmentSummary());
    }

    @GetMapping("/programs/summary")
    @Operation(summary = "Tổng số chương trình hiến máu")
    public ResponseEntity<Map<String, Object>> getProgramSummary() {
        return ResponseEntity.ok(dashboardService.getProgramSummary());
    }

    @PostMapping("/appointments/monthly")
    @Operation(summary = "Thống kê đơn hiến máu theo tháng và trạng thái")
    public ResponseEntity<List<AppointmentMonthlyStatsDTO>> getMonthlyStats(
            @RequestBody DashboardFilterRequest request) {
        return ResponseEntity.ok(dashboardService.getAppointmentStatsByMonth(request.getStartDate(), request.getEndDate()));
    }

    @GetMapping("/appointments/top-users")
    @Operation(summary = "Top 10 người hiến máu nhiều nhất")
    public ResponseEntity<List<TopUserDTO>> getTopUsers() {
        return ResponseEntity.ok(dashboardService.getTopUsers());
    }

    @GetMapping("/appointments/top-programs")
    @Operation(summary = "Top 10 chương trình có nhiều người hiến máu nhất")
    public ResponseEntity<List<TopProgramDTO>> getTopPrograms() {
        return ResponseEntity.ok(dashboardService.getTopPrograms());
    }

    @PostMapping("/programs/monthly")
    @Operation(summary = "Thống kê số chương trình theo tháng và năm")
    public ResponseEntity<List<ProgramMonthlyStatsDTO>> getProgramStatsByMonthSimple(
            @RequestBody MonthlyFilterRequest request) {

        List<ProgramMonthlyStatsDTO> result = dashboardService.getProgramStatsByMonth(
                request.getYear(), request.getMonth()
        );
        return ResponseEntity.ok(result);
    }


}

