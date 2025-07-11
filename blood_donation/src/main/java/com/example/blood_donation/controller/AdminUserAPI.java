package com.example.blood_donation.controller;

import com.example.blood_donation.dto.AdminUserDTO;
import com.example.blood_donation.dto.AdminUserResponseDTO;
import com.example.blood_donation.dto.CreateAdminUserDTO;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.service.AdminUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "api")
public class AdminUserAPI {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public List<User> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return adminUserService.getUserById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AdminUserResponseDTO> createUser(
            @RequestBody CreateAdminUserDTO createDto) {
        AdminUserResponseDTO responseDTO = adminUserService.createUserByAdmin(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody AdminUserDTO adminDTO) {
        UserDTO updated = adminUserService.updateUserByAdmin(id, adminDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/lookup")
    public ResponseEntity<Long> getUserIdByPhone(@RequestParam String phone) {
        Long userId = adminUserService.findUserIdByPhone(phone);
        return ResponseEntity.ok(userId);
    }
}

