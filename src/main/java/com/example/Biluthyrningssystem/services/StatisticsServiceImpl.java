// Niklas Einarsson

package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.dto.StatisticsDTO;
import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
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

    // TODO Lägga till exception för startDate-endDate. Kontrollera så att startDate är tidigare än endDate.
    @Override
    public Map<String, Long> getMostRentedBrandForPeriod(String startDate, String endDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        List<Orders> allOrders = orderRepository.findAll();

        Map<String, Long> brandCounts = new HashMap<>();
        for (Orders order : allOrders) {
            LocalDate hireStart = order.getHireStartDate().toLocalDate();

            if (hireStart.isEqual(start) || hireStart.isBefore(end) && hireStart.isAfter(start) || hireStart.isEqual(end)) {
                Car car = order.getCar();
                if (car != null) {
                    String brand = car.getBrand();
                    brandCounts.put(brand, brandCounts.getOrDefault(brand, 0L) + 1);
                }
            }
        }

        return brandCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> Map.of(entry.getKey(), entry.getValue()))
                .orElse(Collections.emptyMap());
    }


    // TODO Lägga till exception för carId.
    @Override
    public Map<String, Object> getRentalCountByCar(Long carId) {

        List<Orders> allOrders = orderService.getAllOrders();
        long count = allOrders.stream().filter(order -> order.getCar() != null && order.getCar().getId() == carId).count();
        System.out.println(count);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("car id", carId);
        result.put("count", count);

        return result;
    }

    @Override
    public Map<Integer, Long> getRentalDurationsByDays() {
        List<Orders> allOrders = orderService.getAllOrders();

        return allOrders.stream().filter(order -> order.getHireStartDate() != null && order.getHireEndDate() != null)
                .map(order -> {
                    long days = ChronoUnit.DAYS.between(order.getHireStartDate().toLocalDate(), order.getHireEndDate().toLocalDate());
                    return (int) days;
                })
                .collect(Collectors.groupingBy(days -> days, Collectors.counting()));
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
