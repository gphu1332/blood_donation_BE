package com.example.blood_donation.controller;

import com.example.blood_donation.dto.LoginRequest;
import com.example.blood_donation.dto.RegisterRequest;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationAPI {

    @Autowired
    AuthenticationService authenticationService;

    /**
     * API Đăng ký người dùng
     */
    @PostMapping("/api/register")
    @Operation(
            summary = "Đăng ký tài khoản",
            description = "Tạo mới tài khoản người dùng dựa trên thông tin cung cấp"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng ký thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ")
    })
    public ResponseEntity<UserDTO> register(@Valid @org.springframework.web.bind.annotation.RequestBody RegisterRequest user) {
        UserDTO newUser = authenticationService.register(user);
        return ResponseEntity.ok(newUser);
    }

    /**
     * API Đăng nhập người dùng
     */
    @PostMapping("/api/login")
    @Operation(
            summary = "Đăng nhập hệ thống",
            description = "Đăng nhập bằng tài khoản (username/email) và mật khẩu"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công, trả về token và thông tin người dùng"),
            @ApiResponse(responseCode = "400", description = "Tài khoản hoặc mật khẩu không đúng")
    })
    public ResponseEntity<UserDTO> login(@org.springframework.web.bind.annotation.RequestBody LoginRequest loginRequest) {
        UserDTO user = authenticationService.login(loginRequest);
        return ResponseEntity.ok(user);
    }
}
