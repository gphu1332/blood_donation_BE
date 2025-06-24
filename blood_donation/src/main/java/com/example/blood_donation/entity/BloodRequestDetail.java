package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@IdClass(BloodRequestDetailId.class)
public class BloodRequestDetail {
    @Id
    private Long reqID;
    private int packVolume;
    private int packCount;
    @Id
    @ManyToOne
    @JoinColumn(name = "BloodType")
    private BloodType bloodType;
    @ManyToOne
    @JoinColumn(name = "ReqID", insertable = false, updatable = false)
    private BloodRequest bloodRequest;
}
