package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

    @Entity
    @Data
    @Table(name = "Blood_Unit")
    public class BloodUnit {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long bloodUnitID;
        private int volume;
        private LocalDate dateImport;
        private LocalDate expiryDate;
        @Column(unique = true)
        private String bloodSerialCode;
        @ManyToOne
        @JoinColumn(name = "BloodType")
        private BloodType bloodType;
        // 1 người hiến máu có thể có nhiều túi máu trong kho máu
        @ManyToOne
        @JoinColumn(name = "DonID")
        private DonationDetail donationDetail;
        //1 người staff có thể quản lý nhiều túi máu trong kho máu
        @ManyToOne
        @JoinColumn(name = "StaID")
        private Staff staff;
        // Mỗi đơn yêu cầu máu có thể yêu cầu nhiều túi máu trong kho máu
        @ManyToOne
        @JoinColumn(name = "reqID")
        private BloodRequest request;
    }
