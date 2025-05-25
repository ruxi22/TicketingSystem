package com.ticketing.transport.controller;

import com.ticketing.transport.entity.TransportRoute;
import com.ticketing.transport.service.TransportRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class AdminController {

    private final TransportRouteService routeService;

    @Autowired
    public AdminController(TransportRouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/routes/edit/{id}")
    public String showEditRouteForm(@PathVariable Long id, Model model) {
        TransportRoute route = routeService.getRouteById(id);
        if (route == null) {
            model.addAttribute("error", "Route not found");
            return "redirect:/admin/routes";
        }
        model.addAttribute("route", route);
        return "admin/edit-route";
    }

    @PostMapping("/admin/routes/edit/{id}")
    public String updateRoute(@PathVariable Long id,
                              @RequestParam String routeNumber,
                              @RequestParam String transportType,
                              @RequestParam String departureLocation,
                              @RequestParam String arrivalLocation,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate,
                              @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime departureTime,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate arrivalDate,
                              @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime arrivalTime,
                              @RequestParam Double price,
                              RedirectAttributes redirectAttributes) {
        try {
            TransportRoute route = routeService.getRouteById(id);
            if (route == null) {
                redirectAttributes.addFlashAttribute("error", "Route not found");
                return "redirect:/admin/routes";
            }

            // Update route properties
            route.setRouteNumber(routeNumber);
            route.setTransportType(transportType);
            route.setDepartureLocation(departureLocation);
            route.setArrivalLocation(arrivalLocation);
            route.setDepartureTime(departureDate.atTime(departureTime));
            route.setArrivalTime(arrivalDate.atTime(arrivalTime));
            route.setPrice(price);

            // Save updated route
            routeService.updateRoute(route);

            redirectAttributes.addFlashAttribute("success", "Route updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update route: " + e.getMessage());
        }
        return "redirect:/admin/routes";
    }
    @GetMapping("/admin/routes/toggle/{id}")
    public String toggleRouteStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            TransportRoute route = routeService.getRouteById(id);
            if (route == null) {
                redirectAttributes.addFlashAttribute("error", "Route not found");
                return "redirect:/admin/routes";
            }

            // Toggle the active status
            boolean newStatus = !route.isActive();
            routeService.toggleRouteStatus(id, newStatus);

            String statusMessage = newStatus ? "activated" : "deactivated";
            redirectAttributes.addFlashAttribute("success", "Route " + statusMessage + " successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update route status: " + e.getMessage());
        }
        return "redirect:/admin/routes";
    }
}