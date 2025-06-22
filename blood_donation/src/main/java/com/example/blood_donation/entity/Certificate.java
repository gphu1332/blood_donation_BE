package com.example.blood_donation.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "certificates")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long CerID;
    private LocalDate issueDate;
    // Mỗi lần hiến máu chỉ tạo ra một chứng chỉ
    @OneToOne
    @JoinColumn(name = "DonID")
    private DonationDetail donation;
    // Mỗi thành viên có thể có nhiều chứng chỉ
    @ManyToOne
    @JoinColumn(name = "MemID")
    private

}
