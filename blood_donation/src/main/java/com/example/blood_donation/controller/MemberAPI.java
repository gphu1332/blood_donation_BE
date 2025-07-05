package com.example.blood_donation.controller;

import com.example.blood_donation.dto.CreateUpdateMemberRequest;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.service.MemberService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/staff/members")
@PreAuthorize("hasRole('STAFF')")
@SecurityRequirement(name = "api")
public class MemberAPI {

    @Autowired
    private MemberService memberService;

    // Lấy tất cả user MEMBER
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllMembers() {
        List<UserDTO> dtos = memberService.getAllMemberUsers()
                .stream()
                .map(memberService::mapToUserDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Lấy chi tiết user MEMBER theo ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getMemberById(@PathVariable Long id) {
        User user = memberService.getMemberUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or not MEMBER"));
        return ResponseEntity.ok(memberService.mapToUserDTO(user));
    }

    // Tạo mới MEMBER
    @PostMapping
    public ResponseEntity<UserDTO> createMember(@RequestBody CreateUpdateMemberRequest request) {
        User created = memberService.createMember(request);
        return new ResponseEntity<>(memberService.mapToUserDTO(created), HttpStatus.CREATED);
    }

    // Cập nhật MEMBER
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateMember(
            @PathVariable Long id,
            @RequestBody CreateUpdateMemberRequest request
    ) {
        User updated = memberService.updateMember(id, request);
        return ResponseEntity.ok(memberService.mapToUserDTO(updated));
    }

    // Xóa MEMBER
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        User user = memberService.getMemberUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or not MEMBER"));
        memberService.deleteUser(user.getUserID());
        return ResponseEntity.noContent().build();
    }
}
