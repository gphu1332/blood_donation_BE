package com.example.blood_donation.dto;

import lombok.Data;

@Data
public class MonthlyFilterRequest {
    private int month; // 1–12
    private int year;  // ví dụ: 2025
}
