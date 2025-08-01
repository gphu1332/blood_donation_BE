package com.example.blood_donation.entity;


import com.example.blood_donation.enums.TypeBlood;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloodRequestDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeBlood typeBlood;

    private int packVolume;
    private int packCount;

    @ManyToOne
    @JoinColumn(name = "req_id", nullable = false)
    private BloodRequest bloodRequest;
}
