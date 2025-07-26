package com.example.blood_donation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
@Data
public class BloodRequestDTO {
    private Boolean isEmergency;
    private Long medId;
    private List<BloodRequestDetailDTO> details;
}
