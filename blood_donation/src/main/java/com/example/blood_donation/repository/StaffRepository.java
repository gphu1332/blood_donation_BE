package com.example.blood_donation.repository;

import com.example.blood_donation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<User, Long> {
}
