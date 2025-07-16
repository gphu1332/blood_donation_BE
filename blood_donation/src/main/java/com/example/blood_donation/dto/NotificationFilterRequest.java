package com.example.blood_donation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(description = "Bộ lọc cho danh sách thông báo")
public class NotificationFilterRequest {
    @Schema(description = "ID người dùng để lọc", example = "1")
    private Long userId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(description = "Ngày bắt đầu lọc (ISO format)", example = "2025-07-01T00:00:00")
    private LocalDateTime fromDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(description = "Ngày kết thúc lọc (ISO format)", example = "2025-07-11T23:59:59")
    private LocalDateTime toDate;
}
