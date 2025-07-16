package com.example.blood_donation.dto;

import com.example.blood_donation.enums.TypeBlood;
import lombok.Data;

@Data
public class BloodRequestDetailDTO {
    private TypeBlood typeBlood;
    private int packVolume;
    private int packCount;
}
