package com.example.blood_donation.repository;

import com.example.blood_donation.entity.BloodRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
    List<BloodRequest> findByHandledBy_IdAndIsDeletedFalse(Long staffId);

    List<BloodRequest> findByMedicalStaff_IdAndIsDeletedFalse(Long medId);
}
