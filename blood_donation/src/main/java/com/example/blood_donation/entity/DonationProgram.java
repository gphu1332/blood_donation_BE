package com.example.blood_donation.entity;

import com.example.blood_donation.enums.ProgramStatus;
import com.example.blood_donation.enums.TypeBlood;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(nullable = false)
    @ColumnDefault("0")
    private boolean deleted = false;

    @Transient
    private ProgramStatus status;

    public ProgramStatus getStatus() {
        LocalDate today = LocalDate.now();

        if (startDate != null && endDate != null) {
            if (today.isBefore(startDate)) {
                return ProgramStatus.NOT_STARTED;
            } else if ((today.isEqual(startDate) || today.isAfter(startDate)) && today.isBefore(endDate.plusDays(1))) {
                return ProgramStatus.ACTIVE;
            } else {
                return ProgramStatus.FINISHED;
            }
        }

        return null; // nếu thiếu ngày
    }
}