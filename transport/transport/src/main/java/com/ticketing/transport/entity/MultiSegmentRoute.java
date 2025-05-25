package com.ticketing.transport.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class MultiSegmentRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double totalPrice;

    @OneToMany(cascade = CascadeType.ALL)
    private List<TransportRoute> segments;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public List<TransportRoute> getSegments() {
        return segments;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setSegments(List<TransportRoute> segments) {
        this.segments = segments;
    }
}
