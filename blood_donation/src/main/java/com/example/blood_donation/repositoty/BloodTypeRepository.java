package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.BloodType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodTypeRepository extends JpaRepository<BloodType, Long> {
}
