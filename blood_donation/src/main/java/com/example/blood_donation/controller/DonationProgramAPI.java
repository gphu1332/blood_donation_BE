package com.example.blood_donation.controller;

import com.example.blood_donation.dto.DonationProgramDTO;
import com.example.blood_donation.dto.DonationProgramResponse;
import com.example.blood_donation.dto.ProgramStatisticsDTO;
import com.example.blood_donation.service.DonationProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Donation Program API", description = "Quản lý chương trình hiến máu")
public class DonationProgramAPI {

    @Autowired
    private DonationProgramService service;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả chương trình hiến máu")
    @ApiResponse(responseCode = "200", description = "Thành công")
    public List<DonationProgramResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết chương trình theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy chương trình"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chương trình")
    })
    public ResponseEntity<DonationProgramResponse> getById(
            @Parameter(description = "ID chương trình") @PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo mới chương trình hiến máu", description = "ADMIN tạo chương trình, hệ thống sẽ gán người tạo theo tài khoản đang đăng nhập")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền")
    })
    public ResponseEntity<DonationProgramResponse> create(
            @RequestBody DonationProgramDTO dto,
            Principal principal
    ) {
        DonationProgramResponse created = service.create(dto, principal.getName());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật chương trình hiến máu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chương trình")
    })
    public ResponseEntity<DonationProgramResponse> update(
            @Parameter(description = "ID chương trình") @PathVariable Long id,
            @RequestBody DonationProgramDTO dto
    ) {
        DonationProgramResponse updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa chương trình hiến máu theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chương trình")
    })
    public ResponseEntity<String> delete(
            @Parameter(description = "ID chương trình cần xóa") @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Xóa chương trình thành công!");
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm chương trình theo ngày và thành phố")
    @ApiResponse(responseCode = "200", description = "Tìm kiếm thành công")
    public ResponseEntity<List<DonationProgramResponse>> searchByDateRangeAndCity(
            @Parameter(description = "Ngày tổ chức") @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

            @Parameter(description = "ID thành phố") @RequestParam("cityId") Long cityId
    ) {
        List<DonationProgramResponse> results = service.searchByDateInRangeAndCityID(date, cityId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search-range")
    @Operation(summary = "Tìm kiếm chương trình theo khoảng ngày")
    @ApiResponse(responseCode = "200", description = "Tìm kiếm thành công")
    public ResponseEntity<List<DonationProgramResponse>> searchByDateRange(
            @Parameter(description = "Ngày bắt đầu") @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Ngày kết thúc (tùy chọn)") @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<DonationProgramResponse> results = service.searchByDateRange(startDate, endDate);
        return ResponseEntity.ok(results);
    }

    // Kim

    @GetMapping("/{id}/statistics")
    @Operation(summary = "Lấy thống kê chương trình hiến máu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy thống kê thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy chương trình")
    })
    public ResponseEntity<ProgramStatisticsDTO> getStatisticsByProgramId(
            @Parameter(description = "ID chương trình") @PathVariable Long id) {
        ProgramStatisticsDTO statistics = service.getStatisticsByProgramId(id);
        return ResponseEntity.ok(statistics);
    }
}
