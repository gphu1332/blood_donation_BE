package com.example.blood_donation.dto;

import com.example.blood_donation.enums.TypeBlood;
import lombok.Data;

@Data
public class BloodUnitSearchDTO {
    private String bloodSerialCode;
    private TypeBlood typeBlood;

}
