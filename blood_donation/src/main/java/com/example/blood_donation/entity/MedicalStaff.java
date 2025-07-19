package com.example.blood_donation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;

@Entity
@Data
@PrimaryKeyJoinColumn(name = "id")
public class MedicalStaff extends User{
    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;
}
