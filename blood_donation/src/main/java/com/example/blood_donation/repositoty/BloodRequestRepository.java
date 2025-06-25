package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.BloodRequest;
import com.example.blood_donation.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
    List<BloodRequest> findByReqStatusIn(List<Status> statuses);
}
