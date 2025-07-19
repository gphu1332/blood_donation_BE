package com.example.blood_donation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopUserDTO {
    private String fullName;
    private long totalAppointments;
}
