package com.example.blood_donation.entity;

import com.example.blood_donation.enums.Gender;
import com.example.blood_donation.enums.Role;
import com.example.blood_donation.enums.TypeBlood;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long userID;

    public String userName;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "Password invalid!")
    public String password;

    @Email
    public String email;

    @Pattern(regexp = "^(84|0[3|5|7|8|9])[0-9]{8}$", message = "Phone invalid!")
    public String phone;

    public String address;

    @Pattern(regexp = "^\\d{12}$", message = "CCCD invalid!")
    public String cccd;

    @Enumerated(EnumType.STRING)
    public TypeBlood typeBlood;
    @Enumerated(EnumType.STRING)
    public Role role;
    @Enumerated(EnumType.STRING)
    public Gender gender;
}
