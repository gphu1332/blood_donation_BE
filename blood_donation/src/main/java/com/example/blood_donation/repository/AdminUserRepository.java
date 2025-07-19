package com.example.blood_donation.repository;

import com.example.blood_donation.entity.User;
import com.example.blood_donation.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndDeletedFalse(Long id);
    List<User> findByRoleInAndDeletedFalse(List<Role> roles);
    long countByRoleAndDeletedFalse(Role role);

}

