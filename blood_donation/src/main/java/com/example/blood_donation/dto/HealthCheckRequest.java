package com.example.blood_donation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HealthCheckRequest {
    @NotNull
    @Schema(description = "Ngày kiểm tra sức khỏe", example = "2025-07-11")
    private LocalDate checkDate;

    @NotNull
    @DecimalMin(value = "30.0")
    @DecimalMax(value = "200.0")
    @Schema(description = "Cân nặng (kg)", example = "65.5")
    private Double weight;

    @NotNull
    @DecimalMin(value = "5.0")
    @DecimalMax(value = "20.0")
    @Schema(description = "Mức hemoglobin (g/dL)", example = "13.2")
    private Double hemoglobinLevel;

    @NotBlank
    @Schema(description = "Huyết áp", example = "120/80")
    private String bloodPressure;

    @NotNull
    @DecimalMin(value = "35.0")
    @DecimalMax(value = "42.0")
    @Schema(description = "Nhiệt độ cơ thể (°C)", example = "36.7")
    private Double temperature;

    @NotNull
    @Schema(description = "Đủ điều kiện hiến máu hay không", example = "true")
    private Boolean eligible;

    @Schema(description = "Ghi chú thêm", example = "Sức khỏe tốt")
    private String note;

    @NotNull
    @Schema(description = "Id mô tả hiến máu", example = "1")
    private Long donationProgramId;
}
