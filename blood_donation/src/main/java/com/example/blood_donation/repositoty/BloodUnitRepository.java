package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.BloodUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodUnitRepository extends JpaRepository<BloodUnit, Long> {
    List<BloodUnit> findByBloodType_BloodType(String bloodType);
}
