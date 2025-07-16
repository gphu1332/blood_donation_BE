package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
}
