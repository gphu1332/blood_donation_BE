package com.example.blood_donation.entity;


import com.example.blood_donation.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloodRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reqID;

    private String isEmergency;
    private Boolean isDeleted = false;
    private LocalDate reqCreateDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "medid")
    private MedicalStaff medicalStaff;

    @ManyToOne
    @JoinColumn(name = "handled_by_id")
    private User handledBy;

    @OneToMany(mappedBy = "bloodRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BloodRequestDetail> details = new ArrayList<>(); // ✅ KHÔNG dùng List.of()
}

