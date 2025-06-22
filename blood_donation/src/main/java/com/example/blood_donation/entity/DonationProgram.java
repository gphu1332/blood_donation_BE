package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class DonationProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String proName;
    private LocalDate dateCreated;

    // Mỗi Program được tổ chức tại 1 Location
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    // Mỗi Program có nhiều Slot
    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Slot> slots;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;
}


