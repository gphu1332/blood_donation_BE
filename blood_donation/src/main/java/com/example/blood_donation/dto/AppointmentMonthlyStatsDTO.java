package com.example.blood_donation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppointmentMonthlyStatsDTO {
    private String date; // "yyyy-MM"
    private long fulfilled;
    private long cancelled;
}