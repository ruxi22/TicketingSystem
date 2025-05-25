package com.ticketing.transport.controller;

import com.ticketing.transport.entity.Ticket;
import com.ticketing.transport.entity.TransportRoute;
import com.ticketing.transport.service.TicketService;
import com.ticketing.transport.service.TransportRouteService;
import com.ticketing.transport.service.MultiSegmentRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ticketing.transport.entity.MultiSegmentRoute;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RouteController {

    private final TransportRouteService routeService;
    private final TicketService ticketService;


    @Autowired
    public RouteController(TransportRouteService routeService, TicketService ticketService) {
        this.routeService = routeService;
        this.ticketService = ticketService;
    }

    @GetMapping("/user/routes")
    public String showRouteSearch(Model model) {
        model.addAttribute("today", LocalDate.now());
        return "user/route-search";
    }

    @GetMapping("/user/routes/search")
    public String searchRoutes(
            @RequestParam String departure,
            @RequestParam String arrival,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String transportType,
            Model model) {

        // Rute individuale
        List<TransportRoute> routes = routeService.searchRoutesByDate(departure, arrival, date, transportType);

        // Rute multi-segment
        List<MultiSegmentRoute> multiRoutes = multiSegmentRouteService.searchMultiRoutes(departure, arrival, date);


        model.addAttribute("routes", routes);
        model.addAttribute("multiRoutes", multiRoutes);  // Adaugă în model
        model.addAttribute("departure", departure);
        model.addAttribute("arrival", arrival);
        model.addAttribute("date", date);
        model.addAttribute("transportType", transportType);
        model.addAttribute("today", LocalDate.now());

        return "user/route-search";
    }


    @PostMapping("/user/routes/{routeId}/confirm")
    public String confirmPurchase(@PathVariable Long routeId,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        try {
            // Set a maximum of 5 tickets per route per user
            int maxTicketsPerRoute = 5;

            // Create ticket with price and ticket limit
            Ticket ticket = ticketService.purchaseTicket(userDetails.getUsername(), routeId, maxTicketsPerRoute);

            // Add success message
            model.addAttribute("success", "Ticket purchased successfully!");
            model.addAttribute("ticket", ticket);

            return "user/ticket-details";

        } catch (Exception e) {
            model.addAttribute("error", "Failed to purchase ticket: " + e.getMessage());
            return "user/route-search";
        }
    }

    @GetMapping("/user/route-tickets")
    public String viewUserTickets(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        List<Ticket> tickets = ticketService.getUserTickets(userDetails.getUsername());
        model.addAttribute("tickets", tickets);
        return "user/tickets";
    }

    @GetMapping("/admin/routes")
    public String manageRoutes(Model model) {
        List<TransportRoute> routes = routeService.getAllRoutes();
        model.addAttribute("routes", routes);
        return "admin/routes";
    }

    @Autowired
    private MultiSegmentRouteService multiSegmentRouteService;

    @PostMapping("/admin/routes/add")
    public String addMultiSegmentRoute(
            @RequestParam(required = false) String busFrom,
            @RequestParam(required = false) String busTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate busDepartureDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime busDepartureTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate busArrivalDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime busArrivalTime,

            @RequestParam(required = false) String trainFrom,
            @RequestParam(required = false) String trainTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate trainDepartureDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime trainDepartureTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate trainArrivalDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime trainArrivalTime,

            @RequestParam(required = false) String metroFrom,
            @RequestParam(required = false) String metroTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate metroDepartureDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime metroDepartureTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate metroArrivalDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "HH:mm") LocalTime metroArrivalTime,

            @RequestParam Double price
    ) {

        List<TransportRoute> segments = new ArrayList<>();

        if (busFrom != null && busTo != null &&
                busDepartureDate != null && busDepartureTime != null &&
                busArrivalDate != null && busArrivalTime != null) {

            TransportRoute bus = new TransportRoute();
            bus.setRouteNumber("BUS-" + busFrom + "-" + busTo);
            bus.setTransportType("BUS");
            bus.setDepartureLocation(busFrom);
            bus.setArrivalLocation(busTo);
            bus.setDepartureTime(busDepartureDate.atTime(busDepartureTime));
            bus.setArrivalTime(busArrivalDate.atTime(busArrivalTime));
            bus.setPrice(price / 3);
            segments.add(bus);
        }


        // TRAIN
        if (trainFrom != null && trainTo != null &&
                trainDepartureDate != null && trainDepartureTime != null &&
                trainArrivalDate != null && trainArrivalTime != null) {

            TransportRoute train = new TransportRoute();
            train.setRouteNumber("TRAIN-" + trainFrom + "-" + trainTo);
            train.setTransportType("TRAIN");
            train.setDepartureLocation(trainFrom);
            train.setArrivalLocation(trainTo);
            train.setDepartureTime(trainDepartureDate.atTime(trainDepartureTime));
            train.setArrivalTime(trainArrivalDate.atTime(trainArrivalTime));
            train.setPrice(price / 3);
            segments.add(train);
        }

        // METRO
        if (metroFrom != null && metroTo != null &&
                metroDepartureDate != null && metroDepartureTime != null &&
                metroArrivalDate != null && metroArrivalTime != null) {

            TransportRoute metro = new TransportRoute();
            metro.setRouteNumber("METRO-" + metroFrom + "-" + metroTo);
            metro.setTransportType("METRO");
            metro.setDepartureLocation(metroFrom);
            metro.setArrivalLocation(metroTo);
            metro.setDepartureTime(metroDepartureDate.atTime(metroDepartureTime));
            metro.setArrivalTime(metroArrivalDate.atTime(metroArrivalTime));
            metro.setPrice(price / 3);
            segments.add(metro);
        }

        MultiSegmentRoute multi = new MultiSegmentRoute();
        multi.setName(busFrom + " → " + (metroTo != null ? metroTo : trainTo != null ? trainTo : busTo));
        multi.setTotalPrice(price);
        multi.setSegments(segments);

        if (!segments.isEmpty()) {
            System.out.println("Adding route with departure date: " + segments.get(0).getDepartureTime().toLocalDate());
        }

        multiSegmentRouteService.save(multi);
        return "redirect:/admin/routes";
    }



    @PostMapping("/admin/routes/add-recurring")
    public String addRecurringRoute(
            @RequestParam String routeNumber,
            @RequestParam String transportType,
            @RequestParam String departureLocation,
            @RequestParam String arrivalLocation,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime departureTime,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime arrivalTime,
            @RequestParam Double price,
            @RequestParam(value = "days", required = false) List<String> days) {

        // Convert string days to DayOfWeek enum values
        List<DayOfWeek> daysOfWeek = new ArrayList<>();
        if (days != null) {
            for (String day : days) {
                daysOfWeek.add(DayOfWeek.valueOf(day));
            }
        }

        routeService.createRecurringRoute(
                routeNumber, transportType, departureLocation, arrivalLocation,
                departureTime, arrivalTime, price, daysOfWeek);

        return "redirect:/admin/routes";
    }



    @GetMapping("/admin/routes/initialize")
    public String initializeRoutes() {
        routeService.initializeSampleRoutes();
        return "redirect:/admin/routes";
    }

    @PostMapping("/user/routes/multi/{multiId}/confirm")
    public String confirmMultiRoutePurchase(@PathVariable Long multiId,
                                            @AuthenticationPrincipal UserDetails userDetails,
                                            Model model) {
        try {
            MultiSegmentRoute multiRoute = multiSegmentRouteService.getById(multiId);
            int maxTicketsPerRoute = 5;

            Ticket ticket = ticketService.purchaseMultiSegmentTicket(userDetails.getUsername(), multiRoute, maxTicketsPerRoute);

            model.addAttribute("success", "Multi-segment ticket purchased successfully!");
            model.addAttribute("ticket", ticket);

            return "user/ticket-details";

        } catch (Exception e) {
            model.addAttribute("error", "Failed to purchase multi-route ticket: " + e.getMessage());
            return "user/route-search";
        }
    }




}