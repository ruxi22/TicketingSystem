package com.ticketing.transport.service;

import com.ticketing.transport.entity.Notification;
import com.ticketing.transport.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Notification createNotification(String title, String message, LocalDateTime scheduledTime, String targetUsername) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setScheduledTime(scheduledTime);
        notification.setTargetUsername(targetUsername);
        notification.setSent(false);
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(String username) {
        // Using the query method from your repository
        return notificationRepository.findSentNotificationsForUser(username);
    }

    @Scheduled(fixedRate = 10000) // Check every 10 seconds
    public void checkAndSendScheduledNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> pendingNotifications =
                notificationRepository.findByScheduledTimeLessThanEqualAndSentFalse(now);

        for (Notification notification : pendingNotifications) {
            // Only send if the scheduled time has passed
            if (notification.getScheduledTime().isBefore(now) || notification.getScheduledTime().isEqual(now)) {
                if (notification.getTargetUsername() == null) {
                    // Broadcast to all users
                    messagingTemplate.convertAndSend("/topic/notifications", notification);
                } else {
                    // Send to specific user
                    messagingTemplate.convertAndSendToUser(
                            notification.getTargetUsername(),
                            "/queue/notifications",
                            notification
                    );
                }

                notification.setSent(true);
                notificationRepository.save(notification);
            }
        }
    }
}