package com.example.blood_donation.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentDTO {
    private Long userID;
    private Long slotID;
    private LocalDate date;
}
