package com.example.blood_donation.dto;

import com.example.blood_donation.entity.Adress;
import com.example.blood_donation.enums.Gender;
import com.example.blood_donation.enums.TypeBlood;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberUpdateRequest {
    private String fullName;
    private String email;
    private String phone;
    private Adress address;
    private LocalDate birthdate;
    private String cccd;
    private TypeBlood typeBlood;
    private Gender gender;
}
