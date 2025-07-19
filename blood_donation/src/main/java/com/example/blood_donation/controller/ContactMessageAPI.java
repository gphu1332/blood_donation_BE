package com.example.blood_donation.controller;

import com.example.blood_donation.dto.ContactMessageDTO;
import com.example.blood_donation.service.ContactMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactMessageAPI {

    @Autowired
    private ContactMessageService contactMessageService;

    @PostMapping
    @Operation(summary = "Gửi lời nhắn liên hệ", description = "Người dùng gửi thắc mắc hoặc liên hệ qua form.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gửi lời nhắn thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<String> sendMessage(@Valid @RequestBody ContactMessageDTO dto) {
        contactMessageService.saveMessage(dto);
        return ResponseEntity.ok("Gửi lời nhắn thành công!");
    }
}
