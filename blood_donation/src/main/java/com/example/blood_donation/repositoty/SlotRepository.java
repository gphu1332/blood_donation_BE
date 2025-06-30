package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByProgramId(Long programId);
}
