package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.BloodRequestDetail;
import com.example.blood_donation.entity.BloodRequestDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodRequestDetailRepository extends JpaRepository<BloodRequestDetail, BloodRequestDetailId> {
}
