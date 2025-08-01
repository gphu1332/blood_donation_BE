package com.example.blood_donation.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BloodRequestResponseDTO {
    private Long reqID;
    private String isEmergency;
    private String status;
    private LocalDate reqCreateDate;
    private List<BloodRequestDetailDTO> details;
    private String medicalStaffName;
    private String medicalStaffEmail;
    private Long handleById;
    private String handleByName;
    private String handleByEmail;
}
