package com.example.blood_donation.dto;

import com.example.blood_donation.enums.TypeBlood;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DonationDetailDTO {
    private Long donID;
    private Integer donAmount;
    private LocalDate donDate;
    private TypeBlood bloodType;
    private Long appointmentId;
    private Long memberId;
    private Long staffId;
}
