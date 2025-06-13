package com.example.blood_donation.entity;

import com.example.blood_donation.enums.RequestStatus;
import com.example.blood_donation.enums.RequestType;
import com.example.blood_donation.enums.TypeBlood;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "blood_request")
public class BloodRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resId;
    private LocalDate resDateCreated;
    private Integer quantityML;
    @Enumerated(EnumType.STRING)
    private TypeBlood resBloodType;
    @Enumerated(EnumType.STRING)
    private RequestType resType;
    @Enumerated(EnumType.STRING)
    private RequestStatus resStatus;
    @ManyToOne
    @JoinColumn(name = "StaID")
    private Staff staff;
    @ManyToOne
    @JoinColumn(name = "MedID")
    private MedicalStaff medicalStaff;
    @OneToMany(mappedBy = "blood_request", cascade = CascadeType.ALL)
    private List<BloodRequestPriority> priorityList;



}
