package com.example.blood_donation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Yêu cầu đặt lại mật khẩu bằng OTP")
public class ResetPasswordRequest {
    private String email;

    @Schema(description = "Mật khẩu mới", example = "NewPassword123!")
    @NotBlank
    private String newPassword;

    @Schema(description = "Xác nhận lại mật khẩu mới", example = "NewPassword123!")
    @NotBlank
    private String confirmPassword;
}
