package com.ticketing.transport.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.ticketing.transport.entity.Ticket;
import com.ticketing.transport.entity.TransportRoute;
import com.ticketing.transport.entity.User;
import com.ticketing.transport.entity.TravelCard;
import com.ticketing.transport.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import com.ticketing.transport.entity.MultiSegmentRoute;


@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TransportRouteService transportRouteService;

    @Autowired
    private TravelCardService travelCardService;

    public List<Ticket> findByUsername(String username) {
        return ticketRepository.findByUserUsername(username);
    }

    public List<Ticket> getUserTickets(String username) {
        return findByUsername(username);
    }

    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Optional<Ticket> findById(Long id) {
        return ticketRepository.findById(id);
    }

    public Ticket purchaseTicket(String username, Long routeId, int maxTicketsPerRoute) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TransportRoute route = transportRouteService.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        TravelCard travelCard = user.getTravelCard();
        if (travelCard == null) {
            throw new RuntimeException("You don't have a travel card. Please add one to your account.");
        }

        BigDecimal routePrice = BigDecimal.valueOf(route.getPrice()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal cardBalance = travelCard.getBalance().setScale(2, RoundingMode.HALF_UP);

        if (cardBalance.compareTo(routePrice) < 0) {
            throw new RuntimeException("Insufficient funds. Please top up your travel card.");
        }

        // Deduct the ticket price
        travelCardService.deductMoney(username, routePrice);

        // Create and save the ticket
        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setRoute(route);
        ticket.setPrice(routePrice);
        ticket.setPurchaseDateTime(LocalDateTime.now());
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setTicketStatus("VALID");
        ticket.setValidFrom(LocalDateTime.now());
        ticket.setValidTo(route.getArrivalTime());

        return ticketRepository.save(ticket);
    }

    public byte[] generateTicketPdf(Long ticketId, String username) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);

        if (ticketOpt.isEmpty() || !ticketOpt.get().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Ticket not found or access denied");
        }

        Ticket ticket = ticketOpt.get();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("E-Ticket", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            TransportRoute route = ticket.getRoute();
            User user = ticket.getUser();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            document.add(new Paragraph("Ticket ID: " + ticket.getId()));
            document.add(new Paragraph("Passenger: " + user.getUsername()));
            document.add(new Paragraph("From: " + route.getDepartureLocation()));
            document.add(new Paragraph("To: " + route.getArrivalLocation()));
            document.add(new Paragraph("Date: " + route.getDepartureTime().format(dateFormatter)));
            document.add(new Paragraph("Time: " + route.getDepartureTime().format(timeFormatter)));
            document.add(new Paragraph("Transport Type: " + route.getTransportType()));
            document.add(new Paragraph("Price: $" + ticket.getPrice()));
            document.add(new Paragraph("Purchase Date: " + ticket.getPurchaseDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));
            document.add(new Paragraph("Status: " + ticket.getTicketStatus()));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Thank you for using our service!"));

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating PDF ticket", e);
        }

        return outputStream.toByteArray();
    }


    public Ticket purchaseMultiSegmentTicket(String username, MultiSegmentRoute route, int maxTicketsPerRoute) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TravelCard travelCard = user.getTravelCard();
        if (travelCard == null) {
            throw new RuntimeException("You don't have a travel card. Please add one to your account.");
        }

        BigDecimal routePrice = BigDecimal.valueOf(route.getTotalPrice()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal cardBalance = travelCard.getBalance().setScale(2, RoundingMode.HALF_UP);

        if (cardBalance.compareTo(routePrice) < 0) {
            throw new RuntimeException("Insufficient funds. Please top up your travel card.");
        }

        travelCardService.deductMoney(username, routePrice);

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        ticket.setMultiRoute(route);
        ticket.setPrice(routePrice);
        ticket.setPurchaseDateTime(LocalDateTime.now());
        ticket.setTicketStatus("VALID");

        if (!route.getSegments().isEmpty()) {
            ticket.setValidFrom(route.getSegments().get(0).getDepartureTime());
            ticket.setValidTo(route.getSegments().get(route.getSegments().size() - 1).getArrivalTime());
        }

        return ticketRepository.save(ticket);
    }


    public void updateExpiredTickets() {
        LocalDateTime now = LocalDateTime.now();
        List<Ticket> validTickets = ticketRepository.findByTicketStatus("VALID");

        for (Ticket ticket : validTickets) {
            if (ticket.getValidTo() != null && now.isAfter(ticket.getValidTo())) {
                ticket.setTicketStatus("EXPIRED");
                ticketRepository.save(ticket);
            }
        }
    }

    public int countUserTicketsForRoute(String username, Long routeId) {
        return ticketRepository.countByUserUsernameAndRouteId(username, routeId);
    }

}
