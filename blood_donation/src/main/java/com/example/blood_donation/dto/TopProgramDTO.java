package com.example.blood_donation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProgramDTO {
    private String programName;
    private long totalDonors;
}
