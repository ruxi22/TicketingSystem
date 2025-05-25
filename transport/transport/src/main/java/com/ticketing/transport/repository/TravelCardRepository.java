// src/main/java/com/ticketing/transport/repository/TravelCardRepository.java
package com.ticketing.transport.repository;

import com.ticketing.transport.entity.TravelCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TravelCardRepository extends JpaRepository<TravelCard, Long> {
    Optional<TravelCard> findByUserUsername(String username);
}