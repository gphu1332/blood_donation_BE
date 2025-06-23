package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Member extends User {
    private LocalDate memDateCreated;
    private String memJob;
    private String emergencyNoti;

    // Một Member có thể có nhiều DonationDetail
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DonationDetail> donationDetails;
}
