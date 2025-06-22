package com.example.blood_donation.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Donation_Details")
public class DonationDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long DonID;
    private Integer DonAmount;
    private LocalDate DonDate;
    // Mỗi lần hiến máu chỉ hiến một loại  máu
    @OneToOne
    @JoinColumn(name = "BloodType")
    @Enumerated(EnumType.STRING)

}
