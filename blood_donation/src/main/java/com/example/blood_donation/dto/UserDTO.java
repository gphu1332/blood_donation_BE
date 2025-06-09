package com.example.blood_donation.dto;

import com.example.blood_donation.enums.Gender;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.enums.TypeBlood;
import lombok.Data;

@Data
public class UserDTO {
    public long userID;
    public String username;
    public String email;
    public String phone;
    public String address;
    public String cccd;
    public TypeBlood typeBlood;
    public Role role;
    public Gender gender;
    public String token;
}
