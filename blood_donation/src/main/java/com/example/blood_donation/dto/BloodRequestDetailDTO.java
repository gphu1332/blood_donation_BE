package com.example.blood_donation.dto;

import com.example.blood_donation.enums.TypeBlood;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class BloodRequestDetailDTO {
    @Enumerated(EnumType.STRING)
    private TypeBlood typeBlood;
    private int packVolume;
    private int packCount;
}
