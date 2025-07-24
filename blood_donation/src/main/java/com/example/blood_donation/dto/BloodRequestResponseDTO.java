package com.example.blood_donation.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BloodRequestResponseDTO {
    private Long reqID;
    private Boolean isEmergency;
    private String status;
    private LocalDate reqCreateDate;
    private List<BloodRequestDetailDTO> details;
}
