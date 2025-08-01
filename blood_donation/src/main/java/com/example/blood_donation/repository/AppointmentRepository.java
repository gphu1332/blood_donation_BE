package com.example.blood_donation.repository;

import com.example.blood_donation.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a WHERE a.program.startDate = :date")
    List<Appointment> findAppointmentsForProgramDate(@Param("date") LocalDate date);
}
