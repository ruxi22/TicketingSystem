package com.ticketing.transport.service;

import com.ticketing.transport.entity.MultiSegmentRoute;
import com.ticketing.transport.repository.MultiSegmentRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.List;

@Service
public class MultiSegmentRouteService {

    @Autowired
    private MultiSegmentRouteRepository repository;

    public void save(MultiSegmentRoute route) {
        repository.save(route);
    }

    public List<MultiSegmentRoute> getAll() {
        return repository.findAll();
    }

    public List<MultiSegmentRoute> searchMultiRoutes(String from, String to, LocalDate date) {
        return repository.findAll().stream()
                .filter(route ->
                        !route.getSegments().isEmpty() &&
                                route.getSegments().get(0).getDepartureLocation().equalsIgnoreCase(from) &&
                                route.getSegments().get(route.getSegments().size() - 1).getArrivalLocation().equalsIgnoreCase(to) &&
                                (date == null || route.getSegments().get(0).getDepartureTime().toLocalDate().equals(date))
                )
                .toList();
    }






    public MultiSegmentRoute getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("MultiSegmentRoute not found"));
    }

}
