package com.example.blood_donation.repository;


import com.example.blood_donation.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByUsernameAndDeletedFalse(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
