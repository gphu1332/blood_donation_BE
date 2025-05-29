package com.example.blood_donation.api;

import com.example.blood_donation.entity.User;
import com.example.blood_donation.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationAPI {
    @Autowired
    AuthenticationService authenticationService;

    //api -> service -> repository

    @PostMapping("/api/register")
    public ResponseEntity register(@RequestBody User user) {
        User newUser = authenticationService.register(user);
        return ResponseEntity.ok(newUser);
    }

}
