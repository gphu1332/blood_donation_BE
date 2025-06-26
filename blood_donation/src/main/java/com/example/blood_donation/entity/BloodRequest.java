package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.engine.spi.Status;

import java.time.LocalDate;
import java.util.List;

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

    // 1 đơn yêu cầu máu có thể yêu cầu nhiều túi máu với thể tích và số lượng khác nhau
    @OneToMany(mappedBy = "reqID", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BloodRequestDetail> details;
}
