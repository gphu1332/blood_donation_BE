package com.example.blood_donation.dto;

import com.example.blood_donation.enums.Gender;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.enums.TypeBlood;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateAdminUserDTO {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String cccd;
    private LocalDate birthdate;
    private Gender gender;
    private TypeBlood typeBlood;
    private Role role;

}
