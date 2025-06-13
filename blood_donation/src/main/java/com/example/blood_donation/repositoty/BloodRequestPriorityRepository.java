package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.BloodRequestPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodRequestPriorityRepository extends JpaRepository<BloodRequestPriority, Integer> {
    List<BloodRequestPriority> findByBloodRequest_ResIdOrderByPriorityOrderAsc(Integer resId);
}
