package com.example.blood_donation.repository;

import com.example.blood_donation.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepositorry extends JpaRepository<Hospital, Long> {
}
