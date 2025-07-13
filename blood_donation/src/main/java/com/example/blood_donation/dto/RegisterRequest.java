package com.example.blood_donation.dto;

import com.example.blood_donation.enums.Gender;
import com.example.blood_donation.enums.TypeBlood;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Họ tên không được để trống")
    @Schema(description = "Họ và tên đầy đủ", example = "Nguyễn Văn A")
    private String fullName;

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    @Schema(description = "Địa chỉ email", example = "abc@example.com")
    private String email;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 4, message = "Tên đăng nhập tối thiểu 4 ký tự")
    @Schema(description = "Tên đăng nhập", example = "nguyenvana")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên")
    @Schema(description = "Mật khẩu", example = "Abc123@")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    @Schema(description = "Xác nhận lại mật khẩu", example = "Abc123@")
    private String confirmPassword;
}


