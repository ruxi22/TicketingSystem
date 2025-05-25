package com.ticketing.transport.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class TransportRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String routeNumber;
    private String transportType; // BUS, TRAIN, METRO
    private String departureLocation;
    private String arrivalLocation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Integer durationMinutes;
    private Double price;

    // Fields for recurring schedules
    private Boolean isRecurring = false;
    private String recurringDays; // Comma-separated days of week (e.g., "MONDAY,WEDNESDAY,FRIDAY")


    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRouteNumber() { return routeNumber; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }

    public String getTransportType() { return transportType; }
    public void setTransportType(String transportType) { this.transportType = transportType; }

    public String getDepartureLocation() { return departureLocation; }
    public void setDepartureLocation(String departureLocation) { this.departureLocation = departureLocation; }

    public String getArrivalLocation() { return arrivalLocation; }
    public void setArrivalLocation(String arrivalLocation) { this.arrivalLocation = arrivalLocation; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Boolean getIsRecurring() { return isRecurring; }
    public void setIsRecurring(Boolean recurring) { isRecurring = recurring; }

    public String getRecurringDays() { return recurringDays; }
    public void setRecurringDays(String recurringDays) { this.recurringDays = recurringDays; }

    private String departure;

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

        // existing fields

        private boolean active = true;

        // getters and setters
        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }


    @Entity
    public class MultiSegmentRoute {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name; // Ex: "Dragasani to Bucuresti via Caracal"

        @OneToMany(cascade = CascadeType.ALL)
        @JoinColumn(name = "multi_route_id")
        private List<TransportRoute> segments;

        private Double totalPrice;
    }

}