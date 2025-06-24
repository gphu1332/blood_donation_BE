package com.example.blood_donation.entity;

import com.example.blood_donation.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

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
    public Status ReqStatus;
    //Mỗi MedicalStaff có thể tạo nhiều đơn BloodRequest
    @ManyToOne
    @JoinColumn(name = "MedID")
    private MedicalStaff medicalStaff;

}
