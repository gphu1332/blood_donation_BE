package com.example.blood_donation.controller;

import com.example.blood_donation.dto.CityDTO;
import com.example.blood_donation.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/city")
@SecurityRequirement(name = "api")
@Tag(name = "City Management API", description = "API dành cho Admin quản lý địa điểm tổ chức hiến máu")
public class CityAPI {

    @Autowired
    private CityService cityService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả địa điểm (city)")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    public List<CityDTO> getAllLocations() {
        return cityService.getAllLocations();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin địa điểm theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy địa điểm"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy địa điểm")
    })
    public ResponseEntity<CityDTO> getLocation(
            @Parameter(description = "ID của địa điểm cần lấy") @PathVariable Long id) {
        return ResponseEntity.ok(cityService.getLocationById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Tạo địa điểm mới")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo địa điểm thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<CityDTO> createLocation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin địa điểm cần tạo") @RequestBody CityDTO dto) {
        return ResponseEntity.ok(cityService.createLocation(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật địa điểm theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy địa điểm")
    })
    public ResponseEntity<CityDTO> updateLocation(
            @Parameter(description = "ID địa điểm cần cập nhật") @PathVariable Long id,
            @RequestBody CityDTO dto) {
        return ResponseEntity.ok(cityService.updateLocation(id, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa địa điểm theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy địa điểm")
    })
    public ResponseEntity<?> deleteLocation(
            @Parameter(description = "ID địa điểm cần xóa") @PathVariable Long id) {
        cityService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
