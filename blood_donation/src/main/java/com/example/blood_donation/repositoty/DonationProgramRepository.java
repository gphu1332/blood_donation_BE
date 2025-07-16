package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.DonationProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DonationProgramRepository extends JpaRepository<DonationProgram, Long> {
    List<DonationProgram> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndCity_Id(
            LocalDate date,
            LocalDate date2,
            Long locationId
    );
    List<DonationProgram> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
            LocalDate endDate,
            LocalDate startDate
    );

    @Query("SELECT p FROM DonationProgram p WHERE " +
            "p.address.id = :addressId AND " +
            "(:startDate <= p.endDate AND :endDate >= p.startDate)")
    List<DonationProgram> findConflictingPrograms(@Param("addressId") Long addressId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT p FROM DonationProgram p WHERE " +
            "p.address.id = :addressId AND " +
            "(:startDate <= p.endDate AND :endDate >= p.startDate) AND " +
            "p.id <> :programId")
    List<DonationProgram> findConflictingProgramsExcludingSelf(@Param("addressId") Long addressId,
                                                               @Param("startDate") LocalDate startDate,
                                                               @Param("endDate") LocalDate endDate,
                                                               @Param("programId") Long programId);


}
