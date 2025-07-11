package com.example.blood_donation.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.web.bind.annotation.GetMapping;

@Entity
@Data
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Adress location;
}
