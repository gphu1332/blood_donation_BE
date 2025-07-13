package com.example.blood_donation.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MedicalStaffDTO {
    private String fullName;
    private LocalDate birthdate;
    private Long hospitalId;
}
