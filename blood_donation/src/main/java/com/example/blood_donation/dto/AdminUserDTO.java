package com.example.blood_donation.dto;

import com.example.blood_donation.enums.Gender;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.enums.TypeBlood;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AdminUserDTO {
    private String username;   // Cho phép đổi username
    private String password;   // Cho phép đổi mật khẩu
    private String fullName;
    private String email;
    private String phone;
    // Replace AdressDTO with individual fields
    private String addressName;
    private Double latitude;
    private Double longitude;
    private String cccd;
    private LocalDate birthdate;
    private Gender gender;
    private TypeBlood typeBlood;
    private Role role;
    private Long hospitalId;
}
