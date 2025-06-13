package com.example.blood_donation.entity;

import com.example.blood_donation.enums.TypeBlood;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "blood_request_priority")
public class BloodRequestPriority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer priorityOrder;
    @Enumerated(EnumType.STRING)
    private TypeBlood typeBlood;
    @ManyToOne
    @JoinColumn(name = "ResID")
    private BloodRequest bloodRequest;
}
