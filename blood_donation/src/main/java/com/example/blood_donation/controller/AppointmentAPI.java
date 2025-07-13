package com.example.blood_donation.controller;

import com.example.blood_donation.dto.AppointmentDTO;
import com.example.blood_donation.dto.AppointmentRequest;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@SecurityRequirement(name = "api")
@Tag(name = "Appointment API", description = "Quản lý lịch hẹn hiến máu")
public class AppointmentAPI {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('MEMBER')")
    @Operation(summary = "Thành viên tạo lịch hẹn hiến máu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo lịch hẹn thành công"),
            @ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ")
    })
    public ResponseEntity<AppointmentDTO> createAppointment(
            @Parameter(description = "ID của người dùng tạo lịch hẹn") @RequestParam Long userId,
            @Valid @RequestBody AppointmentRequest request) {
        AppointmentDTO dto = appointmentService.createAppointment(userId, request);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/hospital-staff-create")
    @PreAuthorize("hasRole('STAFF')")
    @Operation(summary = "Nhân viên tạo lịch hẹn cho người dùng bằng số điện thoại")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo lịch hẹn thành công"),
            @ApiResponse(responseCode = "400", description = "Thông tin không hợp lệ")
    })
    public ResponseEntity<AppointmentDTO> createAppointmentByPhone(
            @Parameter(description = "Số điện thoại người dùng") @RequestParam String phone,
            @Valid @RequestBody AppointmentRequest request) {
        AppointmentDTO dto = appointmentService.createAppointmentByPhoneAndProgram(phone, request);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'STAFF')")
    @Operation(summary = "Cập nhật trạng thái lịch hẹn")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy lịch hẹn")
    })
    public ResponseEntity<AppointmentDTO> updateStatus(
            @Parameter(description = "ID lịch hẹn") @PathVariable Long id,
            @Parameter(description = "Trạng thái mới") @RequestParam Status status) {
        AppointmentDTO dto = appointmentService.updateStatus(id, status);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'STAFF')")
    @Operation(summary = "Lấy tất cả lịch hẹn")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    public ResponseEntity<List<AppointmentDTO>> getAll() {
        return ResponseEntity.ok(appointmentService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MEMBER')")
    @Operation(summary = "Xem chi tiết lịch hẹn theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy lịch hẹn"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy lịch hẹn")
    })
    public ResponseEntity<AppointmentDTO> getById(
            @Parameter(description = "ID lịch hẹn") @PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'STAFF')")
    @Operation(summary = "Xóa lịch hẹn theo ID (chỉ dành cho STAFF hoặc HOSPITAL_STAFF)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy lịch hẹn")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID lịch hẹn cần xóa") @PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/with-permission")
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'STAFF', 'MEMBER')")
    @Operation(summary = "Xóa lịch hẹn có xác thực quyền người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "403", description = "Không có quyền hoặc lịch hẹn đã hoàn thành")
    })
    public ResponseEntity<Void> deleteWithPermission(
            @Parameter(description = "ID lịch hẹn") @PathVariable Long id,
            @Parameter(description = "ID người dùng muốn xóa") @RequestParam Long userID) {
        appointmentService.deleteAppointmentWithPermission(id, userID);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-user")
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'STAFF', 'MEMBER')")
    @Operation(summary = "Lấy tất cả lịch hẹn của một người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    })
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByUser(
            @Parameter(description = "ID người dùng") @RequestParam Long userId) {
        return ResponseEntity.ok(appointmentService.getByUserId(userId));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'STAFF', 'MEMBER')")
    @Operation(summary = "Lấy lịch sử tất cả các lịch hẹn (đã hoàn thành, đã hủy, v.v.) của user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy thành công")
    })
    public ResponseEntity<List<AppointmentDTO>> getAppointmentHistory(
            @Parameter(description = "ID người dùng") @RequestParam Long userId) {
        return ResponseEntity.ok(appointmentService.getFullHistoryByUser(userId));
    }
}
