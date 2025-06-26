package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.BloodRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
<<<<<<< HEAD
    List<BloodRequest> findByMedicalStaff_UserID(Long id);
    List<BloodRequest> findByStaff_UserID(Long id);
=======
>>>>>>> main
}
