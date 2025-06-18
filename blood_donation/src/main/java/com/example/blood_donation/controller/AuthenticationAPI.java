package com.example.blood_donation.controller;

import com.example.blood_donation.dto.LoginRequest;
import com.example.blood_donation.dto.RegisterRequest;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.service.AuthenticationService;
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
