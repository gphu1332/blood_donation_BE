package com.example.blood_donation.controller;

import com.example.blood_donation.dto.AppointmentDTO;
import com.example.blood_donation.enums.Status;
import com.example.blood_donation.service.AppointmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@SecurityRequirement(name = "api")
public class AppointmentAPI {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentDTO>> getAll() {
        return ResponseEntity.ok(appointmentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//  dat lich appointment (MEMBER)
    @PostMapping
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<AppointmentDTO> createAppointment(
            @RequestParam Long userId,
            @RequestParam Long slotId,
            @RequestParam String date
    ) {
        LocalDate parsedDate = LocalDate.parse(date);
        AppointmentDTO created = appointmentService.createAppointment(userId, slotId, parsedDate);
        return ResponseEntity.ok(created);
    }

//  dat lich appointment (Admin)
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentDTO> createAppointmentByAdmin(
            @RequestParam String phone,
            @RequestParam Long slotId,
            @RequestParam String date
    ) {
        LocalDate parsedDate = LocalDate.parse(date);
        AppointmentDTO created = appointmentService.createAppointmentByPhone(phone, slotId, parsedDate);
        return ResponseEntity.ok(created);
    }


    //  Cap nhat trang thai Appointment
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppointmentDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam Status status
    ) {
        AppointmentDTO updated = appointmentService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }

//  xoa Appointment
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEMBER')")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id, Principal principal) {
        appointmentService.deleteAppointmentWithPermission(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
