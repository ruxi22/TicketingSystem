package com.ticketing.transport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "travel_card")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_number", unique = true)
    private String cardNumber;

    @Column(name = "is_active")
    private boolean active;

    private BigDecimal balance;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "payment_method")
    private String paymentMethod;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public Long getId() {
        return id;
    }



    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
