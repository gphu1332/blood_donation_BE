package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.Appointment;
import com.example.blood_donation.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a WHERE a.program.startDate = :date")
    List<Appointment> findAppointmentsForProgramDate(@Param("date") LocalDate date);
}
