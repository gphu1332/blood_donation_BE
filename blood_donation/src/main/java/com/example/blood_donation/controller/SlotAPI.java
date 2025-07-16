package com.example.blood_donation.controller;

import com.example.blood_donation.dto.SlotRequest;
import com.example.blood_donation.dto.SlotResponse;
import com.example.blood_donation.service.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@SecurityRequirement(name = "api")
@Tag(name = "Slot API", description = "Quản lý các khung giờ hiến máu")
public class SlotAPI {

    @Autowired
    private SlotService slotService;

    @PostMapping
    @Operation(summary = "Tạo khung giờ hiến máu mới", description = "Tạo một slot mới để đặt lịch hiến máu")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo slot thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<SlotResponse> createSlot(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin slot cần tạo") @RequestBody SlotRequest request) {
        SlotResponse response = slotService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin khung giờ theo ID", description = "Trả về chi tiết một slot theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy slot"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy slot")
    })
    public ResponseEntity<SlotResponse> getSlotById(
            @Parameter(description = "ID của slot cần lấy") @PathVariable Long id) {
        SlotResponse slot = slotService.getSlotById(id);
        return ResponseEntity.ok(slot);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả khung giờ", description = "Trả về toàn bộ các slot hiện có")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách slot thành công")
    })
    public ResponseEntity<List<SlotResponse>> getAllSlots() {
        List<SlotResponse> slots = slotService.getAll();
        return ResponseEntity.ok(slots);
    }
}
