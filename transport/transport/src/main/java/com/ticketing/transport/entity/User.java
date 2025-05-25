package com.ticketing.transport.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "`user`") // Needed because "user" is a reserved keyword in many SQL dialects
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String name;
    private String surname;
    private String email;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private TravelCard travelCard;


    @OneToMany(mappedBy = "user")
    private List<Ticket> tickets;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public TravelCard getTravelCard() { return travelCard; }
    public void setTravelCard(TravelCard travelCard) { this.travelCard = travelCard; }

    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
