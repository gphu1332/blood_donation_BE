package com.example.blood_donation.repository;

import com.example.blood_donation.entity.BloodUnit;
import com.example.blood_donation.enums.TypeBlood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodUnitRepository extends JpaRepository<BloodUnit, Long> {
    List<BloodUnit> findByTypeBlood(TypeBlood typeBlood);
}
