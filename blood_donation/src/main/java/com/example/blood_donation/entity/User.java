package com.example.blood_donation.entity;

import com.example.blood_donation.enums.Gender;
import com.example.blood_donation.enums.Role;
import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long userID;

    public String userName;
    public String password;

    @Enumerated(EnumType.STRING)
    public Role role;
    @Enumerated(EnumType.STRING)
    public Gender gender;
}
