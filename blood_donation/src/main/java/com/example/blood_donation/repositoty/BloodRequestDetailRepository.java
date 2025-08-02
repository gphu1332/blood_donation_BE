package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.BloodRequestDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodRequestDetailRepository extends JpaRepository<BloodRequestDetail, Long> {
    List<BloodRequestDetail> findByBloodRequest_ReqID(Long reqID);
}
