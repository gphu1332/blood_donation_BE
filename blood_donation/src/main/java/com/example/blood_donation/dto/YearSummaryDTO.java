package com.example.blood_donation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class YearSummaryDTO {
    private int year;
    private long totalPrograms;
    private long totalAppointments;
}
