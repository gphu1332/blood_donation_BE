package com.example.blood_donation.entity;

import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class Staff extends User {
    private LocalDate staDateCreated;

}
