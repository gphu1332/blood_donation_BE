package com.example.blood_donation.dto;

import com.example.blood_donation.enums.Gender;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.enums.TypeBlood;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    public Long userID;
    public String username;
    public String password;
    public String fullName;
    public String email;
    public String phone;
    private LocalDate birthdate;
    public String address;
    public String cccd;
    public TypeBlood typeBlood;
    public Role role;
    public Gender gender;
    public String token;
}
