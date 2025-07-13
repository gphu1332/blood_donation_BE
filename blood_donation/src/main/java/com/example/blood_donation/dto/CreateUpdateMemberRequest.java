package com.example.blood_donation.dto;

import com.example.blood_donation.entity.Adress;
import com.example.blood_donation.enums.Gender;
import com.example.blood_donation.enums.TypeBlood;
import lombok.Data;

@Data
public class CreateUpdateMemberRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private Adress address;
    private String cccd;
    private TypeBlood typeBlood;
    private Gender gender;
}
