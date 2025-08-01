package com.example.blood_donation.repository;

import com.example.blood_donation.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByPrograms_Id(Long programId);

}
