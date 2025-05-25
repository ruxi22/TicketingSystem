package com.ticketing.transport.controller;

import com.ticketing.transport.entity.Notification;
import com.ticketing.transport.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserNotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notifications")
    public String notificationsPage(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        List<Notification> notifications =
                notificationService.getNotificationsForUser(userDetails.getUsername());
        model.addAttribute("notifications", notifications);
        return "user/notifications";
    }

    @GetMapping("/notifications/data")
    @ResponseBody
    public List<Notification> getNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        return notificationService.getNotificationsForUser(userDetails.getUsername());
    }
}