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

    // Mỗi Appointment gắn với 1 User
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // Mỗi Appointment thuộc về một Slot
    @ManyToOne
    @JoinColumn(name = "slot_id")
    private Slot slot;

    // Mỗi Appointment thuộc về một Program
    @ManyToOne
    @JoinColumn(name = "program_id")
    private DonationProgram program;
}
