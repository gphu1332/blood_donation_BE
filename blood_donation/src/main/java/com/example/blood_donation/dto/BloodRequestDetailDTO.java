package com.example.blood_donation.dto;

import lombok.Data;

@Data
public class BloodRequestDetailDTO {
    private String bloodType;
    private int packVolume;
    private int packCount;
}
