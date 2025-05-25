package com.ticketing.transport.repository;

import com.ticketing.transport.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // This query finds notifications that are ready to be sent
    List<Notification> findByScheduledTimeLessThanEqualAndSentFalse(LocalDateTime now);

    // This query returns notifications for the UI display
    @Query("SELECT n FROM Notification n WHERE (n.targetUsername = :username OR n.targetUsername IS NULL) AND n.sent = true ORDER BY n.scheduledTime DESC")
    List<Notification> findSentNotificationsForUser(@Param("username") String username);
}