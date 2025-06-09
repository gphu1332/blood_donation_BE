package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
