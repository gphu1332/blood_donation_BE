package com.example.blood_donation.controller;

import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "api") // yêu cầu bearer token
public class UserAPI {

    @Autowired
    private UserService userService;

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
}
