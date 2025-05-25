package com.ticketing.transport.repository;

import com.ticketing.transport.entity.TransportRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransportRouteRepository extends JpaRepository<TransportRoute, Long> {

    @Query("SELECT r FROM TransportRoute r WHERE " +
            "r.departureLocation = :departure AND " +
            "r.arrivalLocation = :arrival AND " +
            "DATE(r.departureTime) = :date " +
            "ORDER BY r.departureTime")
    List<TransportRoute> findRoutes(
            @Param("departure") String departure,
            @Param("arrival") String arrival,
            @Param("date") LocalDate date);

    @Query("SELECT r FROM TransportRoute r WHERE " +
            "r.departureLocation = :departure AND " +
            "r.arrivalLocation = :arrival AND " +
            "DATE(r.departureTime) = :date AND " +
            "r.transportType = :transportType " +
            "ORDER BY r.departureTime")
    List<TransportRoute> findRoutesByType(
            @Param("departure") String departure,
            @Param("arrival") String arrival,
            @Param("date") LocalDate date,
            @Param("transportType") String transportType);

    @Query("SELECT r FROM TransportRoute r WHERE " +
            "r.departureLocation = :departure AND " +
            "r.arrivalLocation = :arrival AND " +
            "r.isRecurring = true AND " +
            "r.recurringDays LIKE %:dayOfWeek% " +
            "AND (:transportType IS NULL OR :transportType = 'ALL' OR r.transportType = :transportType) " +
            "ORDER BY r.departureTime")
    List<TransportRoute> findRecurringRoutesByDayOfWeek(
            @Param("departure") String departure,
            @Param("arrival") String arrival,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("transportType") String transportType);

    @Query("SELECT r FROM TransportRoute r WHERE r.departureLocation = :departure " +
            "AND r.arrivalLocation = :arrival " +
            "AND DATE(r.departureTime) = :date " +
            "AND r.active = true")
    List<TransportRoute> findActiveRoutes(@Param("departure") String departure,
                                          @Param("arrival") String arrival,
                                          @Param("date") LocalDate date);

    @Query("SELECT r FROM TransportRoute r WHERE r.departureLocation = :departure " +
            "AND r.arrivalLocation = :arrival " +
            "AND DATE(r.departureTime) = :date " +
            "AND r.transportType = :type " +
            "AND r.active = true")
    List<TransportRoute> findActiveRoutesByType(@Param("departure") String departure,
                                                @Param("arrival") String arrival,
                                                @Param("date") LocalDate date,
                                                @Param("type") String type);
}