package com.ticketing.transport.config;

import com.ticketing.transport.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduledTasks {

    @Autowired
    private TicketService ticketService;

    // Run every hour
    @Scheduled(fixedRate = 3600000)
    public void checkExpiredTickets() {
        ticketService.updateExpiredTickets();
    }
}