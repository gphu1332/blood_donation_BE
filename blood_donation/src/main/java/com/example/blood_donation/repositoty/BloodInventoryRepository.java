package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.BloodInventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Integer> {
}
