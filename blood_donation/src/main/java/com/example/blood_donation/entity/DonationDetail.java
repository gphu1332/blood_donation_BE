package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "Donation_Details")
public class DonationDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long donID;

    private Integer donAmount;
    private LocalDate donDate;

    // Mỗi lần hiến máu chỉ hiến một loại máu
    @OneToOne
    @JoinColumn(name = "blood_type_id")
    private BloodType bloodType;

    // Mỗi lần đặt lịch chỉ dẫn đến một lần hiến máu
    @OneToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    // Mỗi Member có thể hiến máu nhiều lần
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;
}
