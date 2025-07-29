package com.example.blood_donation.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
public class BloodRequestDetail {
    @EmbeddedId
    private BloodRequestDetailId id;

    @MapsId("reqId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reqid")
    private BloodRequest bloodRequest;

    private int packVolume;
    private int packCount;
}
