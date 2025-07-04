package com.example.blood_donation.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AppointmentRequest {
    private LocalDate date;
    private Long slotId;
    private Long programId;
    private List<AnswerRequest> answers;
}


