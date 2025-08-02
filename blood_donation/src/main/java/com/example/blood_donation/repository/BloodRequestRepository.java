package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.BloodRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
    List<BloodRequest> findByHandledBy_IdAndIsDeletedFalse(Long staffId);

    List<BloodRequest> findByMedicalStaff_IdAndIsDeletedFalse(Long medId);

    List<BloodRequest> findByStatusAndIsDeletedFalse(Enum status);
}
