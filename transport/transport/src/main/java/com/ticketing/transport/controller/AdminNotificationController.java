package com.ticketing.transport.controller;

import com.ticketing.transport.entity.Notification;
import com.ticketing.transport.service.NotificationService;
import com.ticketing.transport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminNotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/notifications")
    public String notificationsPage(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("now", LocalDateTime.now());
        return "admin/notifications";
    }

    @PostMapping("/notifications/create")
    public String createNotification(
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime scheduledTime,
            @RequestParam(required = false) String targetUsername) {

        notificationService.createNotification(title, message, scheduledTime, targetUsername);
        return "redirect:/admin/notifications";
    }
}