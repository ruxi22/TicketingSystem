package com.ticketing.transport.repository;

import com.ticketing.transport.entity.MultiSegmentRoute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MultiSegmentRouteRepository extends JpaRepository<MultiSegmentRoute, Long> {
}
