package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.BloodRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Integer> {
}
