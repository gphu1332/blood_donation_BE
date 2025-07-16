package com.example.blood_donation.dto;

import com.example.blood_donation.enums.TypeBlood;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateBloodUnitDTO {
    private int volume;
    private LocalDate dateImport;
    private LocalDate expiryDate;
    private String bloodSerialCode;
    private TypeBlood typeBlood;
    private Long donationDetailId;
    private Long staffId;
    private Long bloodRequestId;
}
