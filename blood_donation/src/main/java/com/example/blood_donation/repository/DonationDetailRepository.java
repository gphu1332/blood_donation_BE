package com.example.blood_donation.repository;

import com.example.blood_donation.entity.DonationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DonationDetailRepository extends JpaRepository<DonationDetail, Long> {
    //Kim
    Optional<DonationDetail> findByAppointment_Id(Long appointmentId);

    List<DonationDetail> findByAppointment_IdIn(List<Long> appointmentIds);

}
