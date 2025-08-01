package com.example.blood_donation.repository;

import com.example.blood_donation.entity.User;
import com.example.blood_donation.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<User, Long> {
    List<User> findByRole(Role role);
}
