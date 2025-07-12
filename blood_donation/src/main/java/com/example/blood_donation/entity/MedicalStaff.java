package com.example.blood_donation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class MedicalStaff extends User{
    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;
}
