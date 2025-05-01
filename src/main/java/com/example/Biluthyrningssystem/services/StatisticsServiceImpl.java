// Niklas Einarsson

package com.example.Biluthyrningssystem.services;

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

    // TODO Lägga till mer data för overview
    @Override
    public Map<String, Object> getStatistics() {

        Map<String, Object> statistics = new LinkedHashMap<>();

        Map<String, Double> totalRevenue2025 = getTotalRevenueForPeriod("2025-01-01", "2025-12-31");
        statistics.put("Intäkter 2025", totalRevenue2025.get("Totala intäkter för perioden"));

        Map<String, Double> revenuePerOrder = getAverageCostPerOrder();
        statistics.put("Genomsnittlig intäkt per bokning", revenuePerOrder.get("Genomsnittlig kostnad per bokning"));

        Map<String, Object> endpoints = new LinkedHashMap<>();

        endpoints.put("Tillgängliga endpoints", List.of(
                "/statistics",
                "/statistics/mostrentedbrand/period/{startDate}/{endDate}",
                "/statistics/carrentalcount/{carId}",
                "/statistics/rentaldurations",
                "/statistics/averageordercost",
                "/statistics/revenuepercar",
                "/statistics/revenue/period/{startDate}/{endDate}"
        ));
        statistics.putAll(endpoints);

        return statistics;
    }

    // TODO Lägga till exception för startDate-endDate. Kontrollera så att startDate är tidigare än endDate. + Fixa Loggning.
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
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

//                 return brandCounts.entrySet().stream()
//                .max(Map.Entry.comparingByValue())
//                .map(entry -> Map.of(entry.getKey(), entry.getValue()))
//                .orElse(Collections.emptyMap());
    }


    // TODO Lägga till exception för carId + fixa loggning.
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

    // TODO Exceptions + loggning
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
    // TODO Exceptions + loggning
    @Override
    public Map<String, Double> getAverageCostPerOrder() {

        List<Orders> allOrders = orderService.getAllOrders();

        List<Orders> ordersToCalculate = allOrders.stream().filter(order -> order.getHireStartDate() != null).collect(Collectors.toList());

        if (ordersToCalculate.isEmpty()) {
            return Collections.emptyMap();
        }

        double totalRevenue = ordersToCalculate.stream()
                .mapToDouble(Orders::getTotalPrice)
                .sum();

        double average = totalRevenue / ordersToCalculate.size();
        average = Math.round(average * 100) / 100.0;

        return Map.of("Genomsnittlig kostnad per bokning", average);
    }

    // TODO Exceptions + loggning
    @Override
    public Map<Long, Double> getTotalRevenuePerCar() {

        List<Orders> allOrders = orderService.getAllOrders();

        Map<Long, Double> revenuePerCar = new HashMap<>();

        for (Orders order : allOrders) {
            if (order.getCar() != null) {
                Long carId = order.getCar().getId();
                double totalPrice = order.getTotalPrice();
                revenuePerCar.put(carId, revenuePerCar.getOrDefault(carId, 0.0) + totalPrice);

            }
        }

        return revenuePerCar.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Math.round(e.getValue() * 100) / 100.0
                ));
    }
    // TODO Exceptions + loggning
    @Override
    public Map<String, Double> getTotalRevenueForPeriod(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        List<Orders> allOrders = orderService.getAllOrders();

        double totalRevenue = allOrders.stream()
                .filter(order -> {
                    LocalDate hireStart = order.getHireStartDate().toLocalDate();
                    return (hireStart.isEqual(start) || hireStart.isAfter(start)) &&
                            (hireStart.isEqual(end) || hireStart.isBefore(end));
                })
                .mapToDouble(Orders::getTotalPrice)
                .sum();

        totalRevenue = Math.round(totalRevenue * 100) / 100.0;

        return Map.of("Totala intäkter för perioden", totalRevenue);
    }
}
