package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@IdClass(BloodRequestDetailId.class)
public class BloodRequestDetail {

    @Id
    @Column(name = "ReqID")
    private Long reqID;

    @Id
    private String bloodType;

    private int packVolume;
    private int packCount;

    @ManyToOne
    @JoinColumn(name = "ReqID", insertable = false, updatable = false)
    private BloodRequest bloodRequest;
}
