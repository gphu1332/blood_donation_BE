package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.Adress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdressRepository extends JpaRepository<Adress, Long> {
}
