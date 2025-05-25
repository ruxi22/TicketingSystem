package com.ticketing.transport.service;

import com.ticketing.transport.entity.TravelCard;
import com.ticketing.transport.entity.User;
import com.ticketing.transport.repository.TravelCardRepository;
import com.ticketing.transport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TravelCardService {
    @Autowired
    private TravelCardRepository travelCardRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<TravelCard> getByUsername(String username) {
        return travelCardRepository.findByUserUsername(username);
    }

    public TravelCard createForUsername(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            // Return existing card if present
            Optional<TravelCard> existing = travelCardRepository.findByUserUsername(username);
            if (existing.isPresent()) {
                return existing.get();
            }
            // Create new card
            TravelCard travelCard = new TravelCard();
            travelCard.setUser(userOpt.get());
            travelCard.setBalance(BigDecimal.ZERO);
            return travelCardRepository.save(travelCard);
        }
        throw new IllegalArgumentException("User not found: " + username);
    }

    public void addMoney(String username, BigDecimal amount) {
        travelCardRepository.findByUserUsername(username).ifPresent(card -> {
            card.setBalance(card.getBalance().add(amount));
            travelCardRepository.save(card);
        });
    }

    // In TravelCardService.java

    public boolean existsForUsername(String username) {
        return travelCardRepository.findByUserUsername(username).isPresent();
    }

    public void deductMoney(String username, BigDecimal amount) {
        travelCardRepository.findByUserUsername(username).ifPresent(card -> {
            if (card.getBalance().compareTo(amount) >= 0) {
                card.setBalance(card.getBalance().subtract(amount));
                travelCardRepository.save(card);
            } else {
                throw new RuntimeException("Insufficient balance");
            }
        });
    }
    // In TravelCardService.java
    public void updateCard(TravelCard card) {
        travelCardRepository.save(card);
    }
}