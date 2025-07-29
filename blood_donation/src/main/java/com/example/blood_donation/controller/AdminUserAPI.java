package com.example.blood_donation.controller;

import com.example.blood_donation.dto.AdminUserDTO;
import com.example.blood_donation.dto.AdminUserResponseDTO;
import com.example.blood_donation.dto.CreateAdminUserDTO;
import com.example.blood_donation.dto.UserDTO;
import com.example.blood_donation.entity.User;
import com.example.blood_donation.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class AdminUserAPI {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả người dùng", description = "Chỉ Admin mới có quyền xem tất cả người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công")
    })
    public List<User> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin người dùng theo ID", description = "Admin có thể tra cứu thông tin chi tiết người dùng bằng ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy người dùng"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng")
    })
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID của người dùng") @PathVariable Long id) {
        return adminUserService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Tạo người dùng mới (vai trò bất kỳ)", description = "Admin tạo tài khoản cho người dùng khác")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    })
    public ResponseEntity<AdminUserResponseDTO> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin người dùng cần tạo") @RequestBody CreateAdminUserDTO createDto) {
        AdminUserResponseDTO responseDTO = adminUserService.createUserByAdmin(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin người dùng", description = "Admin chỉnh sửa thông tin người dùng theo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng")
    })
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID người dùng cần cập nhật") @PathVariable Long id,
            @RequestBody AdminUserDTO adminDTO) {
        UserDTO updated = adminUserService.updateUserByAdmin(id, adminDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa người dùng", description = "Admin xóa tài khoản người dùng bằng ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID người dùng cần xóa") @PathVariable Long id) {
        adminUserService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/lookup")
    @Operation(summary = "Tìm ID người dùng theo số điện thoại", description = "Admin có thể tìm ID người dùng dựa trên số điện thoại")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tìm thấy người dùng"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng")
    })
    public ResponseEntity<Long> getUserIdByPhone(
            @Parameter(description = "Số điện thoại người dùng") @RequestParam String phone) {
        Long userId = adminUserService.findUserIdByPhone(phone);
        return ResponseEntity.ok(userId);
    }
}
