package com.example.blood_donation.repository;

import com.example.blood_donation.entity.BloodRequestDetail;
import com.example.blood_donation.entity.BloodRequestDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodRequestDetailRepository extends JpaRepository<BloodRequestDetail, BloodRequestDetailId> {
    List<BloodRequestDetail> findByReqID(Long reqID);
}
