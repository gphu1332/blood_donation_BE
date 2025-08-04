package com.example.blood_donation.repository;

import com.example.blood_donation.entity.Appointment;
import com.example.blood_donation.entity.DonationProgram;
import com.example.blood_donation.enums.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByProgram_Id(Long programId);


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
                WHERE a.status = com.example.blood_donation.enums.Status.FULFILLED
                GROUP BY a.program.id, a.program.proName
                ORDER BY COUNT(a.id) DESC
            """)
    List<Object[]> findTop10Programs();


    @Query("""
                SELECT a.user.fullName, COUNT(a.id)
                FROM Appointment a
                WHERE a.status = com.example.blood_donation.enums.Status.FULFILLED
                GROUP BY a.user.id, a.user.fullName
                ORDER BY COUNT(a.id) DESC
            """)
    List<Object[]> findTop10Users();

    @Query("SELECT COUNT(a) FROM Appointment a WHERE YEAR(a.date) = :year")
    long countByYear(@Param("year") int year);

    @Query("""
                SELECT a.program.proName, COUNT(a.id)
                FROM Appointment a
                WHERE a.status = com.example.blood_donation.enums.Status.FULFILLED
                  AND YEAR(a.date) = :year
                GROUP BY a.program.id, a.program.proName
                ORDER BY COUNT(a.id) DESC
            """)
    List<Object[]> findTop10ProgramsByYear(@Param("year") int year, Pageable pageable);

    @Query("""
                SELECT a.user.fullName, COUNT(a.id)
                FROM Appointment a
                WHERE a.status = com.example.blood_donation.enums.Status.FULFILLED
                  AND YEAR(a.date) = :year
                GROUP BY a.user.id, a.user.fullName
                ORDER BY COUNT(a.id) DESC
            """)
    List<Object[]> findTop10UsersByYear(@Param("year") int year, Pageable pageable);


    long count();

    long countByStatus(Status status);

    @Query("""
                SELECT a FROM Appointment a
                WHERE (a.status = com.example.blood_donation.enums.Status.PENDING
                    OR a.status = com.example.blood_donation.enums.Status.APPROVED)
                  AND a.date < :today
            """)
    List<Appointment> findExpiredAppointments(@Param("today") LocalDate today);

    @Query("""
    SELECT COUNT(a)
    FROM Appointment a
    WHERE a.program.id = :programId
      AND a.status IN ('PENDING', 'APPROVED', 'FULFILLED')
""")
    long countActiveAppointmentsByProgram(@Param("programId") Long programId);

}
