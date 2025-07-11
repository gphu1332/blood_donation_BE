package com.example.blood_donation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Blood Type")
public class BloodType {
    @Id
    private String bloodType;
}
