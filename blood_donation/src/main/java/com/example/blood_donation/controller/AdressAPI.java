package com.example.blood_donation.controller;

import com.example.blood_donation.dto.AdressDTO;
import com.example.blood_donation.service.AdressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@SecurityRequirement(name = "api")
@Tag(name = "Address API", description = "Quản lý thông tin địa chỉ dùng trong hệ thống")
public class AdressAPI {

    @Autowired
    private AdressService adressService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả địa chỉ", description = "Trả về danh sách đầy đủ các địa chỉ trong hệ thống")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    public ResponseEntity<List<AdressDTO>> getAll() {
        return ResponseEntity.ok(adressService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy địa chỉ theo ID", description = "Trả về chi tiết địa chỉ tương ứng với ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy địa chỉ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy địa chỉ")
    })
    public ResponseEntity<AdressDTO> getById(
            @Parameter(description = "ID của địa chỉ") @PathVariable Long id) {
        return ResponseEntity.ok(adressService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Tạo mới địa chỉ", description = "Tạo một bản ghi địa chỉ mới trong hệ thống")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo địa chỉ thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<AdressDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin địa chỉ cần tạo") @RequestBody AdressDTO dto) {
        return ResponseEntity.ok(adressService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật địa chỉ", description = "Cập nhật thông tin địa chỉ theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật địa chỉ thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy địa chỉ")
    })
    public ResponseEntity<AdressDTO> update(
            @Parameter(description = "ID địa chỉ cần cập nhật") @PathVariable Long id,
            @RequestBody AdressDTO dto) {
        return ResponseEntity.ok(adressService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa địa chỉ", description = "Xóa địa chỉ khỏi hệ thống theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Xóa địa chỉ thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy địa chỉ")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID địa chỉ cần xóa") @PathVariable Long id) {
        adressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
