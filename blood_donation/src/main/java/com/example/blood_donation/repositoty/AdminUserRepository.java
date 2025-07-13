package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.User;
import com.example.blood_donation.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminUserRepository extends JpaRepository<User, Long> {
    List<User> findByRoleIn(List<Role> roles);

    long countByRole(Role role);
}
