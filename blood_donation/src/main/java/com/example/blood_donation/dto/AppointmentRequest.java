package com.example.blood_donation.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class AppointmentRequest {
    private LocalDate date;               // Ngày đặt lịch
    private Long slotId;                  // Slot người dùng chọn
    private Long programId;               // Chương trình hiến máu

    // 9 câu trả lời
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String answer5;
    private String answer6;
    private String answer7;
    private String answer8;
    private String answer9;
    private String answer10;
}



