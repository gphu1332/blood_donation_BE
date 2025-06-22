package com.example.blood_donation.dto;

import com.example.blood_donation.enums.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentDTO {
    private Long id;
    private LocalDate date;
    private Status status;
    private Long userID;
    private Long slotID;
    private Long programId;
}
