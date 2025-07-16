package com.example.blood_donation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Admin extends User {
    private LocalDate adDateCreated;

    // Một Admin quản lý nhiều Certificate
    @OneToMany(mappedBy = "admin")
    private List<Certificate> certificates;
}
