package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.DonationProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DonationProgramRepository extends JpaRepository<DonationProgram, Long> {
    List<DonationProgram> findByStartDateAndLocation_Name(LocalDate startDate, String locationName);
}
