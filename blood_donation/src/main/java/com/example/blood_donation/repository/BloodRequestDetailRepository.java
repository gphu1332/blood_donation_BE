package com.example.blood_donation.repository;

import com.example.blood_donation.entity.BloodRequestDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BloodRequestDetailRepository extends JpaRepository<BloodRequestDetail, Long> {
    List<BloodRequestDetail> findByBloodRequest_ReqID(Long reqId);

    @Modifying
    @Query("DELETE FROM BloodRequestDetail d WHERE d.bloodRequest.reqID = :reqId")
    void deleteAllByRequestId(@Param("reqId") Long reqId);

}

