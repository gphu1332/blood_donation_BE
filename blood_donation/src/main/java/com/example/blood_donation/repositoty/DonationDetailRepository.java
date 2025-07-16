package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.DonationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationDetailRepository extends JpaRepository<DonationDetail, Long> {
}
