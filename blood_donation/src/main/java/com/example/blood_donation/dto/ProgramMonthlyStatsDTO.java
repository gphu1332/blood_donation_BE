package com.example.blood_donation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ProgramMonthlyStatsDTO {
    private LocalDate date; // sẽ là ngày đầu tháng (VD: 2025-07-01)
    private long count;
}
