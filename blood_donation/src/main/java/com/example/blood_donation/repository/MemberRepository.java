package com.example.blood_donation.repository;

import com.example.blood_donation.entity.User;
import com.example.blood_donation.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<User, Long> {
    List<User> findByRole(Role role);
    List<User> findByRoleAndDeletedFalse(Role role);
    Optional<User> findByIdAndRoleAndDeletedFalse(Long id, Role role);
}
