package com.example.blood_donation.entity;

import com.example.blood_donation.enums.TypeBlood;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@IdClass(BloodRequestDetailId.class)
public class BloodRequestDetail {

    @Id
    @Column(name = "ReqID")
    private Long reqID;

    @Enumerated(EnumType.STRING)
    private TypeBlood typeBlood;

    private int packVolume;
    private int packCount;

    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "ReqID", insertable = false, updatable = false)
    private BloodRequest bloodRequest;
}
