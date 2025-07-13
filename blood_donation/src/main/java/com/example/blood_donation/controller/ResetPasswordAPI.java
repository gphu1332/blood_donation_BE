package com.example.blood_donation.controller;

import com.example.blood_donation.dto.ResetPasswordRequest;
import com.example.blood_donation.service.ResetPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reset Password", description = "Quy trình đặt lại mật khẩu người dùng ")
@RequestMapping("/api")
@SecurityRequirement(name = "api")
@RestController
public class ResetPasswordAPI {
    @Autowired
    ResetPasswordService resetPasswordService;

    @PostMapping("/generate-otp")
    public ResponseEntity generateOTP(
            @RequestParam String email) {
        return ResponseEntity.ok(resetPasswordService.generateOtp(email));
    }


    @PostMapping("/verify-otp")
    @Operation(summary = "Xác minh mã OTP", description = "Xác minh OTP trước khi cho phép đổi mật khẩu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP hợp lệ"),
            @ApiResponse(responseCode = "400", description = "OTP không hợp lệ hoặc đã hết hạn")
    })
    public ResponseEntity verifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {
      return ResponseEntity.ok(resetPasswordService.verifyOtp(email, otp));
    }

    @PostMapping("reset-password")
    @Operation(summary = "Đặt lại mật khẩu", description = "Đặt lại mật khẩu khi OTP đã được xác minh")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đặt lại mật khẩu thành công"),
            @ApiResponse(responseCode = "400", description = "Mật khẩu không khớp hoặc OTP chưa xác minh")
    })
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        resetPasswordService.resetPassword(request);
        return ResponseEntity.ok("Đặt lại mật khẩu thành công");
    }
}
