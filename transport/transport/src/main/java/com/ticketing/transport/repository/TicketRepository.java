package com.ticketing.transport.repository;

import com.ticketing.transport.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserUsername(String username);

    List<Ticket> findByTicketStatus(String status);

    int countByUserUsernameAndRouteId(String username, Long routeId);

    // Add this method for route deletion check
    List<Ticket> findByRouteId(Long routeId);
}