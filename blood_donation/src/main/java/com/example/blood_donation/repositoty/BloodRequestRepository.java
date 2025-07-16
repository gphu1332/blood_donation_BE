package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.BloodRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
    List<BloodRequest> findByMedicalStaff_Id(Long id);
    List<BloodRequest> findByStaff_Id(Long id);
}
