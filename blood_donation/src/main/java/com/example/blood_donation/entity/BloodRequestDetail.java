package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@IdClass(BloodRequestDetailId.class)
public class BloodRequestDetail {
    @EmbeddedId
    private BloodRequestDetailId id;

    @Id
    private Long reqID;
    private int packVolume;
    private int packCount;
    @Id
    @ManyToOne
    @JoinColumn(name = "BloodType")
    private BloodType bloodType;
    @ManyToOne
    @MapsId("reqID")
    @JoinColumn(name = "ReqID", insertable = false, updatable = false)
    private BloodRequest bloodRequest;
}
