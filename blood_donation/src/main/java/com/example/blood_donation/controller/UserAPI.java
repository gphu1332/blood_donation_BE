package com.example.blood_donation.controller;

import com.example.blood_donation.dto.ChangePasswordRequest;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.service.AuthenticationService;
import com.example.blood_donation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "api") // yêu cầu bearer token
public class UserAPI {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @PutMapping("/{id}")
    @Operation(
        summary = "Cập nhật thông tin người dùng",
        description = "Cập nhật thông tin người dùng có role là MEMBER sau khi đăng nhập"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cập nhật thông tin thành công"),
        @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
        @ApiResponse(responseCode = "401", description = "Không có quyền truy cập"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng")
    })
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID của người dùng cần cập nhật") @PathVariable Long id,
            @RequestBody UserDTO userDTO) {

        UserDTO updated = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/disable-my-account")
    @Operation(
        summary = "Vô hiệu hóa tài khoản của member đang đăng nhập",
        description = "Vô hiệu hóa tài khoản của tài khoản đang đăng nhập trong hệ thống."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vô hiệu hóa tài khoản thành công"),
        @ApiResponse(responseCode = "401", description = "Không có quyền truy cập"),
        @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
        @ApiResponse(responseCode = "400", description = "Tài khoản đã bị vô hiệu hóa trước đó")
    })

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal principal) {
        String username = principal.getName(); // Lấy username từ token
        userService.changePassword(username, request);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }

    public ResponseEntity<String> disableMyAccount() {
        // Lấy user đang đăng nhập trong hê thống
        User currentUser = authenticationService.getCurrentUser();

        userService.disableMyAccount(currentUser.getId());

        return ResponseEntity.ok("Tài khoản của bạn đã được vô hiệu hóa thành công");
    }
}
