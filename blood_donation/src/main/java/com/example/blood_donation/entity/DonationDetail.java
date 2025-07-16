package com.example.blood_donation.entity;

import com.example.blood_donation.enums.TypeBlood;
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

    @Enumerated(EnumType.STRING)
    private TypeBlood typeBlood;

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
