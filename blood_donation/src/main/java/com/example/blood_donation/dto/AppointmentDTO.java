package com.example.blood_donation.dto;

import com.example.blood_donation.enums.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentDTO {
    private Long id;
    private LocalDate date;
    private Status status;
    private String phone;
    private Long slotID;
    private Long programId;
    private String address;         // từ program
    private String timeRange;       // từ slot
    private Long userId;

    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String answer5;
    private String answer6;
    private String answer7;
    private String answer8;
    private String answer9;

}
