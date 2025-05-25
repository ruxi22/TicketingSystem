package com.ticketing.transport.service;

import com.ticketing.transport.entity.Ticket;
import com.ticketing.transport.entity.TransportRoute;
import com.ticketing.transport.repository.TicketRepository;
import com.ticketing.transport.repository.TransportRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransportRouteService {

    private final TransportRouteRepository routeRepository;
    private final TicketRepository ticketRepository;

    @Autowired
    public TransportRouteService(TransportRouteRepository routeRepository, TicketRepository ticketRepository) {
        this.routeRepository = routeRepository;
        this.ticketRepository = ticketRepository;
    }

    public List<TransportRoute> searchRoutes(String departure, String arrival, LocalDate date, String transportType) {
        if (transportType != null && !transportType.isEmpty() && !transportType.equals("ALL")) {
            // Only return active routes
            return routeRepository.findActiveRoutesByType(departure, arrival, date, transportType);
        } else {
            // Only return active routes
            return routeRepository.findActiveRoutes(departure, arrival, date);
        }
    }

    public List<TransportRoute> searchRoutesByDate(String departure, String arrival, LocalDate date, String transportType) {
        // Get standard routes for the specified date
        List<TransportRoute> directRoutes = searchRoutes(departure, arrival, date, transportType);

        // Get the day of week from the requested date
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Find recurring routes for this day of week
        List<TransportRoute> recurringRoutes = routeRepository.findRecurringRoutesByDayOfWeek(
                departure, arrival, dayOfWeek.toString(), transportType);

        // For recurring routes, update their dates to match the requested date
        List<TransportRoute> adjustedRecurringRoutes = new ArrayList<>();
        for (TransportRoute route : recurringRoutes) {
            TransportRoute adjustedRoute = new TransportRoute();

            // Copy all properties
            adjustedRoute.setId(route.getId());
            adjustedRoute.setRouteNumber(route.getRouteNumber());
            adjustedRoute.setTransportType(route.getTransportType());
            adjustedRoute.setDepartureLocation(route.getDepartureLocation());
            adjustedRoute.setArrivalLocation(route.getArrivalLocation());
            adjustedRoute.setPrice(route.getPrice());
            adjustedRoute.setDurationMinutes(route.getDurationMinutes());
            adjustedRoute.setIsRecurring(route.getIsRecurring());
            adjustedRoute.setRecurringDays(route.getRecurringDays());
            adjustedRoute.setActive(route.isActive());

            // Adjust the times to the requested date while keeping the time component
            LocalTime departureTime = route.getDepartureTime().toLocalTime();
            LocalTime arrivalTime = route.getArrivalTime().toLocalTime();

            adjustedRoute.setDepartureTime(departureTime.atDate(date));
            adjustedRoute.setArrivalTime(arrivalTime.atDate(date));

            adjustedRecurringRoutes.add(adjustedRoute);
        }

        // Combine the lists
        directRoutes.addAll(adjustedRecurringRoutes);
        return directRoutes;
    }

    public TransportRoute createRoute(TransportRoute route) {
        // Calculate duration if not set
        if (route.getDurationMinutes() == null && route.getDepartureTime() != null && route.getArrivalTime() != null) {
            long minutes = java.time.Duration.between(route.getDepartureTime(), route.getArrivalTime()).toMinutes();
            route.setDurationMinutes((int) minutes);
        }
        return routeRepository.save(route);
    }

    public TransportRoute createRecurringRoute(
            String routeNumber, String type, String departure, String arrival,
            LocalTime departTime, LocalTime arrivalTime, Double price,
            List<DayOfWeek> daysOfWeek) {

        TransportRoute route = new TransportRoute();
        route.setRouteNumber(routeNumber);
        route.setTransportType(type);
        route.setDepartureLocation(departure);
        route.setArrivalLocation(arrival);

        // Set a reference date (today) just for storing the time
        LocalDate referenceDate = LocalDate.now();
        route.setDepartureTime(departTime.atDate(referenceDate));
        route.setArrivalTime(arrivalTime.atDate(referenceDate));

        // Calculate duration
        long minutes = java.time.Duration.between(departTime, arrivalTime).toMinutes();
        route.setDurationMinutes((int) minutes);

        route.setPrice(price);
        route.setIsRecurring(true);

        // Convert days of week to comma-separated string
        String recurringDays = daysOfWeek.stream()
                .map(DayOfWeek::toString)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        route.setRecurringDays(recurringDays);

        return routeRepository.save(route);
    }

    // Method to initialize sample data
    public void initializeSampleRoutes() {
        // Clear existing routes
        routeRepository.deleteAll();

        // Today's date
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        // Sample TRAIN routes
        createSampleRoute("TR101", "TRAIN", "Bucharest", "Cluj",
                today.atTime(8, 0), today.atTime(14, 30), 120.0);
        createSampleRoute("TR102", "TRAIN", "Cluj", "Bucharest",
                today.atTime(9, 0), today.atTime(15, 30), 120.0);

        createSampleRoute("TR103", "TRAIN", "Bucharest", "Cluj",
                tomorrow.atTime(8, 0), tomorrow.atTime(14, 30), 120.0);

        // Sample BUS routes
        createSampleRoute("BUS201", "BUS", "Bucharest", "Brasov",
                today.atTime(10, 0), today.atTime(12, 30), 50.0);
        createSampleRoute("BUS202", "BUS", "Brasov", "Bucharest",
                today.atTime(13, 0), today.atTime(15, 30), 50.0);

        // Sample METRO routes
        createSampleRoute("M1", "METRO", "Dristor", "Unirii",
                today.atTime(8, 15), today.atTime(8, 30), 2.5);
        createSampleRoute("M2", "METRO", "Unirii", "Pipera",
                today.atTime(8, 35), today.atTime(9, 0), 2.5);

        // Create sample recurring routes
        List<DayOfWeek> weekdays = List.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        );

        List<DayOfWeek> weekends = List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

        // Recurring weekday train
        createRecurringRoute("TR-WD1", "TRAIN", "Bucharest", "Constanta",
                LocalTime.of(7, 30), LocalTime.of(10, 45), 85.0, weekdays);

        // Recurring weekend train
        createRecurringRoute("TR-WE1", "TRAIN", "Bucharest", "Constanta",
                LocalTime.of(9, 0), LocalTime.of(12, 15), 95.0, weekends);

        // Recurring daily bus
        createRecurringRoute("BUS-D1", "BUS", "Bucharest", "Pitesti",
                LocalTime.of(8, 0), LocalTime.of(9, 30), 30.0,
                List.of(DayOfWeek.values()));

        // Recurring metro
        createRecurringRoute("M-D1", "METRO", "Pipera", "Berceni",
                LocalTime.of(7, 0), LocalTime.of(7, 40), 2.5,
                List.of(DayOfWeek.values()));
    }

    private void createSampleRoute(String routeNumber, String type, String departure, String arrival,
                                   LocalDateTime departTime, LocalDateTime arrivalTime, Double price) {
        TransportRoute route = new TransportRoute();
        route.setRouteNumber(routeNumber);
        route.setTransportType(type);
        route.setDepartureLocation(departure);
        route.setArrivalLocation(arrival);
        route.setDepartureTime(departTime);
        route.setArrivalTime(arrivalTime);
        route.setPrice(price);
        route.setIsRecurring(false);
        route.setActive(true);

        // Calculate duration
        long minutes = java.time.Duration.between(departTime, arrivalTime).toMinutes();
        route.setDurationMinutes((int) minutes);

        routeRepository.save(route);
    }

    public TransportRoute getRouteById(Long id) {
        return routeRepository.findById(id).orElse(null);
    }

    public List<TransportRoute> getAllRoutes() {
        return routeRepository.findAll();
    }

    @Transactional
    public void deleteRoute(Long routeId) {
        // Check if there are any tickets for this route
        List<Ticket> tickets = ticketRepository.findByRouteId(routeId);

        if (!tickets.isEmpty()) {
            throw new RuntimeException("Cannot delete route that has associated tickets. Delete the tickets first or mark the route as inactive.");
        }

        // No tickets, safe to delete
        routeRepository.deleteById(routeId);
    }

    @Transactional
    public void deactivateRoute(Long routeId) {
        TransportRoute route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));

        // Set status to inactive instead of deleting
        route.setActive(false);
        routeRepository.save(route);
    }

    // In TransportRouteService.java
    public Optional<TransportRoute> findById(Long id) {
        return routeRepository.findById(id);
    }

    public TransportRoute updateRoute(TransportRoute route) {
        // Calculate duration if needed
        if (route.getDepartureTime() != null && route.getArrivalTime() != null) {
            long minutes = java.time.Duration.between(route.getDepartureTime(), route.getArrivalTime()).toMinutes();
            route.setDurationMinutes((int) minutes);
        }
        return routeRepository.save(route);
    }
    @Transactional
    public void toggleRouteStatus(Long routeId, boolean active) {
        TransportRoute route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        route.setActive(active);
        routeRepository.save(route);
    }
}