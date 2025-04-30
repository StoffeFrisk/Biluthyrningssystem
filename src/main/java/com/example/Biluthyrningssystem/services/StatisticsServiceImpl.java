// Niklas Einarsson

package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.dto.StatisticsDTO;
import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Autowired
    public StatisticsServiceImpl(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }


    @Override
    public List<StatisticsDTO> getStatistics() {
        return List.of();
    }

    @Override
    public Map<String, Long> getMostRentedBrandForPeriod(String startDate, String endDate) {
        return Map.of();
    }

    @Override
    public Map<String, Object> getRentalCountByCar(Long carId) {
        return Map.of();
    }

    @Override
    public Map<Integer, Long> getRentalDurationsByDays() {
        return Map.of();
    }

    @Override
    public double getAverageCostPerOrder() {
        return 0;
    }

    @Override
    public double getTotalRevenuePerCar() {
        return 0;
    }

    @Override
    public double getTotalRevenueForPeriod(String startDate, String endDate) {
        return 0;
    }

}
