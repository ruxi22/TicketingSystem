package com.ticketing.transport.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id", nullable = false)
    private TransportRoute route;

    private LocalDateTime purchaseDateTime;
    private String ticketStatus;
    private BigDecimal price;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    private LocalDateTime purchaseDate;

    @ManyToOne
    private MultiSegmentRoute multiRoute;

    public MultiSegmentRoute getMultiRoute() {
        return multiRoute;
    }

    public void setMultiRoute(MultiSegmentRoute multiRoute) {
        this.multiRoute = multiRoute;
    }



    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public TransportRoute getRoute() { return route; }
    public void setRoute(TransportRoute route) { this.route = route; }

    public LocalDateTime getPurchaseDateTime() { return purchaseDateTime; }
    public void setPurchaseDateTime(LocalDateTime purchaseDateTime) { this.purchaseDateTime = purchaseDateTime; }

    public String getTicketStatus() { return ticketStatus; }
    public void setTicketStatus(String ticketStatus) { this.ticketStatus = ticketStatus; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
}
