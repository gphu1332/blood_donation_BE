package com.example.blood_donation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class BloodType {
    @Id
    private String bloodType;
}
