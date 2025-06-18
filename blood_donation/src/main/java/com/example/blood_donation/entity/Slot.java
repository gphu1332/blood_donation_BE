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
    long slotID;

    String label;
    LocalTime start;
    LocalTime end;
    boolean isDeleted = false;

    @OneToMany(mappedBy = "slot")
    @JsonIgnore
    private List<Appointment> appointments;
}
