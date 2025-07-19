package com.example.blood_donation.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DashboardFilterRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
