package com.example.blood_donation.dto;

import com.example.blood_donation.enums.Gender;
import com.example.blood_donation.enums.TypeBlood;
import lombok.Data;

@Data
public class RegisterRequest {
    private String fullName;
    private String email;
    private String username;
    private String password;
//    private String phone;
//    private String address;
//    private String cccd;
//    private Gender gender;
//    private TypeBlood typeBlood;
    private String comfirmPassword;
}


