package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class HealthCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate checkDate;

    private Double weight;

    private Double hemoglobinLevel;

    private String bloodPressure;

    private Double temperature;

    private Boolean eligible;

    private String note;

    @OneToOne(mappedBy = "healthCheck")
    DonationProgram donationProgram;
}
