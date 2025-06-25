package com.example.blood_donation.entity;

import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class MedicalStaff extends User{
    private LocalDate medStartDate;
}
