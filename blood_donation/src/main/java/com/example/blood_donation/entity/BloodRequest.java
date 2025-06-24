package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.engine.spi.Status;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "BloodRequest")
public class BloodRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ReqID;
    private LocalDate ReqCreateDate;
    private String isEmergency;
    @Enumerated(EnumType.STRING)
    public Status reqStatus;
    //Mỗi MedicalStaff có thể tạo nhiều đơn BloodRequest
    @ManyToOne
    @JoinColumn(name = "MedID")
    private MedicalStaff medicalStaff;
    // Mỗi staff có thể xử lý nhiều đơn yêu cầu máu
    @ManyToOne
    @JoinColumn(name = "StaID")
    private Staff staff;
}
