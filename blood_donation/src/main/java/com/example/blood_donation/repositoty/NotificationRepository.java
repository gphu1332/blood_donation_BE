package com.example.blood_donation.repositoty;

import com.example.blood_donation.entity.Notification;
import com.example.blood_donation.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);

    @Query("""
    SELECT n FROM Notification n
    WHERE (:userId IS NULL OR n.user.userID = :userId)
      AND (:fromDate IS NULL OR n.createdAt >= :fromDate)
      AND (:toDate IS NULL OR n.createdAt <= :toDate)
""")
    List<Notification> findByFilters(@Param("userId") Long userId,
                                     @Param("fromDate") LocalDateTime fromDate,
                                     @Param("toDate") LocalDateTime toDate
                                    );


}
