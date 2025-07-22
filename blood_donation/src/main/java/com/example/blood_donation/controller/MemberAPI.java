package com.example.blood_donation.controller;

import com.example.blood_donation.dto.CreateUpdateMemberRequest;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Member Management", description = "Quản lý người dùng có vai trò MEMBER")
public class MemberAPI {

    @Autowired
    private MemberService memberService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả MEMBER", description = "Chỉ nhân viên STAFF mới có quyền thực hiện")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    public ResponseEntity<List<UserDTO>> getAllMembers() {
        List<UserDTO> dtos = memberService.getAllMemberUsers()
                .stream()
                .map(memberService::mapToUserDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping(path = "/public/usernames")
    @Operation(summary = "Lấy danh sách tên đăng nhập của tất cả MEMBER", description = "API công khai, không yêu cầu xác thực")
    @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<String>> getAllMemberUsernames() {
        List<String> usernames = memberService.getAllMemberUsers()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usernames);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết MEMBER theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy thành viên"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thành viên hoặc không phải MEMBER")
    })
    public ResponseEntity<UserDTO> getMemberById(
            @Parameter(description = "ID thành viên") @PathVariable Long id) {
        User user = memberService.getMemberUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or not MEMBER"));
        return ResponseEntity.ok(memberService.mapToUserDTO(user));
    }

    @PostMapping
    @Operation(summary = "Tạo mới tài khoản MEMBER")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<UserDTO> createMember(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin thành viên mới") @RequestBody CreateUpdateMemberRequest request) {
        User created = memberService.createMember(request);
        return new ResponseEntity<>(memberService.mapToUserDTO(created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin MEMBER")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thành viên")
    })
    public ResponseEntity<UserDTO> updateMember(
            @Parameter(description = "ID thành viên cần cập nhật") @PathVariable Long id,
            @RequestBody CreateUpdateMemberRequest request
    ) {
        User updated = memberService.updateMember(id, request);
        return ResponseEntity.ok(memberService.mapToUserDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa MEMBER theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thành viên hoặc không phải MEMBER")
    })
    public ResponseEntity<Void> deleteMember(
            @Parameter(description = "ID thành viên cần xóa") @PathVariable Long id) {
        User user = memberService.getMemberUserById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or not MEMBER"));
        memberService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }
}