package com.example.blood_donation.dto;

import com.example.blood_donation.enums.Gender;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.enums.TypeBlood;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AdminUserResponseDTO {
    private Long userID;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String cccd;
    private Gender gender;
    private LocalDate birthdate;
    private TypeBlood typeBlood;
    private Role role;
    private String username;
    private Long hospitalId;
    private String hospitalName;
}
