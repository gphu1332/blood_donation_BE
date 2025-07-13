package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthCheckRepository extends JpaRepository<HealthCheck, Long> {
}
