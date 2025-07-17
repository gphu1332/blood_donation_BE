package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "certificates")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cerID;

    private LocalDate issueDate;

    // Mỗi lần hiến máu chỉ tạo ra một chứng chỉ
    @OneToOne
    @JoinColumn(name = "donation_id")
    private DonationDetail donation;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;
    // Một Admin có thể tạo/duyệt nhiều Certificate
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;
}
