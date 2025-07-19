package com.example.blood_donation.repository;

import com.example.blood_donation.entity.Appointment;
import com.example.blood_donation.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a WHERE a.program.startDate = :date")
    List<Appointment> findAppointmentsForProgramDate(@Param("date") LocalDate date);

    @Query("""
        SELECT FUNCTION('DATE_FORMAT', a.date, '%Y-%m') as month,
               a.status,
               COUNT(a)
        FROM Appointment a
        WHERE a.date BETWEEN :startDate AND :endDate
        GROUP BY FUNCTION('DATE_FORMAT', a.date, '%Y-%m'), a.status
    """)
    List<Object[]> countByMonthAndStatus(@Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);

    @Query("""
        SELECT a.program.proName, COUNT(a.id)
        FROM Appointment a
        GROUP BY a.program.id, a.program.proName
        ORDER BY COUNT(a.id) DESC
    """)
    List<Object[]> findTop10Programs();

    @Query("""
        SELECT a.user.fullName, COUNT(a.id)
        FROM Appointment a
        GROUP BY a.user.id, a.user.fullName
        ORDER BY COUNT(a.id) DESC
    """)
    List<Object[]> findTop10Users();

    long count();

    long countByStatus(Status status);
}
