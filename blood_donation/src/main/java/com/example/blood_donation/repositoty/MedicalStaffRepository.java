package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.MedicalStaff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalStaffRepository extends JpaRepository<MedicalStaff, Long> {
}
