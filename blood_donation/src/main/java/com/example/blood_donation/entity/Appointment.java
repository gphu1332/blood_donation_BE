package com.example.blood_donation.entity;

import com.example.blood_donation.enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "appointment")
    private List<User> user;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private Slot slot;
}
