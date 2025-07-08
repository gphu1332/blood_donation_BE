package com.example.blood_donation.entity;

import com.example.blood_donation.enums.TypeBlood;
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
    private LocalDate endDate;
    private LocalDate startDate;

    private String address;

    private Double latitude;
    private Double longitude;

    // Mỗi Program được tổ chức tại 1 Location
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    // Mỗi Program có nhiều Slot
    @ManyToMany
    @JoinTable(
            name = "program_slot",
            joinColumns = @JoinColumn(name = "program_id"),
            inverseJoinColumns = @JoinColumn(name = "slot_id")
    )
    private List<Slot> slots;

    @Enumerated(EnumType.STRING)
    private TypeBlood typeBlood;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    private String contact;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;
}


