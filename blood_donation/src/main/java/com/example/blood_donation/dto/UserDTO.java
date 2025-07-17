package com.example.blood_donation.dto;

import com.example.blood_donation.enums.Gender;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.enums.TypeBlood;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonFormat(pattern = "yyyy-MM-dd")
public class UserDTO {
    public Long id;
    public String username;
    public String password;
    public String fullName;
    public String email;
    public String phone;
    private LocalDate birthdate;
    public AdressDTO address;
    public String cccd;
    public TypeBlood typeBlood;
    public Role role;
    public Gender gender;
    public String token;
    private boolean isDeleted;
}
