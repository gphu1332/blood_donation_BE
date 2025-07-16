package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false)
    private boolean deleted = false;

    // Một Location tổ chức nhiều Program
    @OneToMany(mappedBy = "city")
    private List<DonationProgram> programs;
}


