package com.example.blood_donation.entity;

import jakarta.persistence.Entity;
import org.w3c.dom.Text;

import java.time.LocalDate;

@Entity
public class Member extends User{
    private LocalDate memDateCreated;
    private String memJob;
    private String emergencyNoti;
}
