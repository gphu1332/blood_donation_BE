package com.example.blood_donation.repositoty;


import com.example.blood_donation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
