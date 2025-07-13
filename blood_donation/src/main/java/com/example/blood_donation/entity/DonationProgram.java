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

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Adress address;

    // Mỗi Program được tổ chức tại 1 city
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    // Mỗi Program có 1 hoặc nhiều Slot
    @ManyToMany
    @JoinTable(
            name = "program_slot",
            joinColumns = @JoinColumn(name = "program_id"),
            inverseJoinColumns = @JoinColumn(name = "slot_id")
    )
    private List<Slot> slots;

    @ElementCollection(targetClass = TypeBlood.class)
    @CollectionTable(name = "program_blood_types", joinColumns = @JoinColumn(name = "program_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type")
    private List<TypeBlood> typeBloods;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    private String contact;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "healthCheck_id")
    HealthCheck healthCheck;
}