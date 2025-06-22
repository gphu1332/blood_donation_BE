package com.example.blood_donation.entity;

import jakarta.persistence.*;

import java.lang.reflect.Member;
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
    private BloodType bloodType;
    //Mỗi lần đặt lịch chỉ dẫn đến 1 lần hiến máu
    @OneToOne
    @JoinColumn(name = "AppID")
    private  Appointment appointment;
    //Mỗi thành viên có thể hiến máu nhiều lần
    @ManyToOne
    @JoinColumn(name = "MemID")
    private Member member;
}
