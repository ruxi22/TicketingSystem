package com.ticketing.transport.controller;

import com.ticketing.transport.entity.Ticket;
import com.ticketing.transport.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import com.ticketing.transport.service.NotificationService;
import com.ticketing.transport.service.TransportRouteService;
import com.ticketing.transport.entity.TransportRoute;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.List;

@Controller
public class TicketController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TransportRouteService routeService; // Changed from RouteService

    @Autowired
    private TicketService ticketService;

    // Keep this endpoint for the "My Tickets" nav link
    @GetMapping("/user/purchased-tickets")
    public String viewPurchasedTickets(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        List<Ticket> tickets = ticketService.getUserTickets(userDetails.getUsername());
        model.addAttribute("tickets", tickets);
        return "user/tickets";
    }

    @GetMapping("/user/tickets")
    public String viewTickets(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        List<Ticket> tickets = ticketService.getUserTickets(userDetails.getUsername());

        // Add availability information to the model
        Map<Long, Boolean> ticketAvailability = new HashMap<>();
        for (Ticket ticket : tickets) {
            ticketAvailability.put(ticket.getId(),
                    ticket.getRoute().getArrivalTime().isAfter(java.time.LocalDateTime.now()));
        }

        model.addAttribute("tickets", tickets);
        model.addAttribute("ticketAvailability", ticketAvailability);
        return "user/tickets";
    }

    @GetMapping("/user/tickets/{id}/download")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable Long id,
                                                 @AuthenticationPrincipal UserDetails userDetails) {
        byte[] pdfContent = ticketService.generateTicketPdf(id, userDetails.getUsername());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    @PostMapping("/user/tickets/purchase/{id}")
    public String confirmPurchase(@PathVariable("id") Long id,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        try {
            Ticket ticket = ticketService.purchaseTicket(userDetails.getUsername(), id, Integer.MAX_VALUE);

            // Create notification message
            String notificationMessage = "You have successfully purchased a ticket from " +
                    ticket.getRoute().getDepartureLocation() + " to " + ticket.getRoute().getArrivalLocation() +
                    " departing on " + ticket.getRoute().getDepartureTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

            // Send notification
            notificationService.createNotification(
                    "Ticket Purchase Confirmation",
                    notificationMessage,
                    LocalDateTime.now(),
                    userDetails.getUsername()
            );

            redirectAttributes.addFlashAttribute("success", "Ticket purchased successfully!");
            return "redirect:/user/tickets/" + ticket.getId();
        } catch (Exception e) {
            // Add the error message as a flash attribute
            redirectAttributes.addFlashAttribute("error", e.getMessage());

            // Redirect back to the purchase confirmation page instead of the routes page
            return "redirect:/user/routes/" + id + "/purchase";
        }
    }

    @GetMapping("/user/tickets/{id}")
    public String viewTicketDetails(@PathVariable("id") Long id,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        try {
            Ticket ticket = ticketService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ticket not found"));

            // Check if this ticket belongs to the current user
            if (!ticket.getUser().getUsername().equals(userDetails.getUsername())) {
                throw new RuntimeException("Access denied");
            }

            model.addAttribute("ticket", ticket);
            return "user/ticket-details";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/user/tickets";
        }
    }
    @GetMapping("/user/routes/{id}/purchase")
    public String showPurchaseConfirmation(@PathVariable("id") Long id,
                                           Model model,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the route details using your TransportRouteService
            TransportRoute route = routeService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Route not found"));

            model.addAttribute("route", route);
            return "user/confirm-purchase"; // Render the correct template
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/user/routes"; // Redirect to routes page on error
        }
    }




}