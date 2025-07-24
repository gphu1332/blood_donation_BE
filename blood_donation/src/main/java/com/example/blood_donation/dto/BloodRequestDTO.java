package com.example.blood_donation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
@Data
public class BloodRequestDTO {
    private Boolean isEmergency;
    @NotNull (message = "medId must not be null")
    private Long medId;
    private List<BloodRequestDetailDTO> details;
}
