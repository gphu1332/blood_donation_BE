package com.example.blood_donation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Yêu cầu tạo thông báo mới")
public class NotificationRequest {
    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 100, message = "Tiêu đề không được vượt quá 100 ký tự")
    @Schema(description = "Tiêu đề của thông báo", example = "Lịch hiến máu sắp tới", required = true)
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    @Size(max = 1000, message = "Nội dung không được vượt quá 1000 ký tự")
    @Schema(description = "Nội dung chi tiết của thông báo", example = "Bạn có thể tham gia hiến máu tại TP.HCM vào ngày 15/07", required = true)
    private String message;

    @NotNull(message = "Người nhận không được để trống")
    @Schema(description = "ID người nhận thông báo", example = "3")
    private Long userId;
}
