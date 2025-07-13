package com.example.blood_donation.controller;

import com.example.blood_donation.dto.AppointmentDTO;
import com.example.blood_donation.dto.AppointmentRequest;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.service.AppointmentService;
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
@Tag(name = "Appointment API")
public class AppointmentAPI {

    private final AppointmentService appointmentService;

    /**
     * Thành viên tạo appointment mới.
     */
    @PostMapping
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<AppointmentDTO> createAppointment(
            @RequestParam Long userId,
            @Valid @RequestBody AppointmentRequest request) {
        AppointmentDTO dto = appointmentService.createAppointment(userId, request);
        return ResponseEntity.ok(dto);
    }

    /**
     * Nhân viên bệnh viện tạo appointment cho người dùng.
     */
    @PostMapping("/hospital-staff-create")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<AppointmentDTO> createAppointmentByPhone(
            @RequestParam String phone,
            @Valid @RequestBody AppointmentRequest request) {
        AppointmentDTO dto = appointmentService.createAppointmentByPhoneAndProgram(phone, request);
        return ResponseEntity.ok(dto);
    }

    /**
     * HOSPITAL_STAFF hoặc STAFF cập nhật trạng thái appointment.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'STAFF')")
    public ResponseEntity<AppointmentDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam Status status) {
        AppointmentDTO dto = appointmentService.updateStatus(id, status);
        return ResponseEntity.ok(dto);
    }

    /**
     * Lấy danh sách tất cả appointments.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'STAFF')")
    public ResponseEntity<List<AppointmentDTO>> getAll() {
        List<AppointmentDTO> list = appointmentService.getAll();
        return ResponseEntity.ok(list);
    }

    /**
     * Lấy chi tiết một appointment.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MEMBER')")
    public ResponseEntity<AppointmentDTO> getById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * HOSPITAL_STAFF hoặc STAFF xóa appointment.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'STAFF')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * User hoặc Staff hoặc Hospitall staff xóa appointment nếu chưa hoàn thành.
     */
    @DeleteMapping("/{id}/with-permission")
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'STAFF', 'MEMBER')")
    public ResponseEntity<Void> deleteWithPermission(
            @PathVariable Long id,
            @RequestParam Long userID) {
        appointmentService.deleteAppointmentWithPermission(id, userID);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lấy tất cả các lịch hẹn của 1 người dùng.
     */
    @GetMapping("/by-user")
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'STAFF', 'MEMBER')")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByUser(
            @RequestParam Long userId
    ) {
        List<AppointmentDTO> list = appointmentService.getByUserId(userId);
        return ResponseEntity.ok(list);
    }

    /**
     * Lấy toàn bộ lịch sử appointment của user (bao gồm đã hủy, đã hoàn thành, đang tiến hành...).
     */
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('HOSPITAL_STAFF', 'STAFF', 'MEMBER')")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentHistory(@RequestParam Long userId) {
        List<AppointmentDTO> history = appointmentService.getFullHistoryByUser(userId);
        return ResponseEntity.ok(history);
    }
}
