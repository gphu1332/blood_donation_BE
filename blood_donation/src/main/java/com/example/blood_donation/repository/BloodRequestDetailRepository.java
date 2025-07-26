package com.example.blood_donation.repository;

import com.example.blood_donation.entity.BloodRequestDetail;
import com.example.blood_donation.entity.BloodRequestDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BloodRequestDetailRepository extends JpaRepository<BloodRequestDetail, BloodRequestDetailId> {
    List<BloodRequestDetail> findByBloodRequest_ReqID(Long reqId);

    @Modifying
    @Query("DELETE FROM BloodRequestDetail d WHERE d.bloodRequest.reqID = :reqId")
    void deleteAllByRequestId(@Param("reqId") Long reqId);

}

