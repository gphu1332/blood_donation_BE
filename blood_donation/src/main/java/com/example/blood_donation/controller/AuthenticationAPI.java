package com.example.blood_donation.controller;

import com.example.blood_donation.dto.LoginRequest;
import com.example.blood_donation.dto.RegisterRequest;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "api")
public class AuthenticationAPI {
    @Autowired
    AuthenticationService authenticationService;

    //api -> service -> repository

    @PostMapping("/api/register")
    @Operation(
            summary = "Đăng ký tài khoản",
            description = "Tạo mới tài khoản người dùng dựa trên thông tin cung cấp"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng ký thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
    })
    public ResponseEntity register(@Valid @RequestBody RegisterRequest user) {
        UserDTO newUser = authenticationService.register(user);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/api/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest) {
        UserDTO user = authenticationService.login(loginRequest);
        return ResponseEntity.ok(user);
    }

}
