package com.example.blood_donation.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;


@Entity
@Data
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotID;

    private String label;
    private LocalTime start;
    private LocalTime end;
    private boolean isDeleted = false;

    // Mỗi Slot thuộc về 1 Program
    @ManyToOne
    @JoinColumn(name = "program_id")
    private DonationProgram program;

    @OneToMany(mappedBy = "slot")
    @JsonIgnore
    private List<Appointment> appointments;
}
