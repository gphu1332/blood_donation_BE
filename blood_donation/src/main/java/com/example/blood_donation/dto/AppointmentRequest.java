package com.example.blood_donation.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class AppointmentRequest {
    private LocalDate date;               // Ngày đặt lịch
    private Long slotId;                  // Slot người dùng chọn
    private Long programId;               // Chương trình hiến máu
    private List<AnswerRequest> answers; // Danh sách câu trả lời
}



