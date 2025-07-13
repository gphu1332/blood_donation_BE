package com.example.blood_donation.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DonationDetailDTO {
    private Long donID;
    private Integer donAmount;
    private LocalDate donDate;
    private Long bloodType;
    private Long appointmentId;
    private Long memberId;
    private Long staffId;
}
