// Niklas Einarsson

package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.exceptions.IncorrectCalculationException;
import com.example.Biluthyrningssystem.exceptions.IncorrectInputException;
import com.example.Biluthyrningssystem.exceptions.ResourceNotFoundException;
import com.example.Biluthyrningssystem.repositories.CarRepository;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import org.apache.logging.log4j.LogManager;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(StatisticsServiceImpl.class);
    private static final Logger applicationLogger = LoggerFactory.getLogger("app");


    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final CarRepository carRepository;

    @Autowired
    public StatisticsServiceImpl(OrderRepository orderRepository, OrderService orderService, CarRepository carRepository) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.carRepository = carRepository;
    }

    // TODO Lägga till mer data för overview
    @Override
    public Map<String, Object> getStatistics() {

        logger.info("Endpoint /admin/statistics was used");

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

        logger.info("Endpoint /admin/statistics/mostrentedbrand was used");

        LocalDate start;
        LocalDate end;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            start = LocalDate.parse(startDate, formatter);
            end = LocalDate.parse(endDate, formatter);
        } catch (DateTimeParseException e) {
            logger.warn("Could not parse date {} because wrong format was used.", startDate);
            throw new IncorrectInputException("Statistics", "date input", startDate + " and " + endDate, "yyyy-MM-dd", "Please use the correct date format.");
        }

        if (start.isAfter(end)) {
            System.out.println("Start date is after end date");
            logger.warn("Invalid date because start date {} is after end date {}", startDate, endDate);
            throw new IncorrectInputException("Statistics", "Start-End Dates", ("Start:" + startDate + "->End:" + endDate), "YYYY-MM-DD", "Start date must be BEFORE end date.");
        }

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

        logger.info("Endpoint /admin/statistics/carrentalcount was used");

        Optional<Car> optionalCar = carRepository.findById(carId);
        if (optionalCar.isEmpty()) {
            logger.warn("Could not find car with id " + carId);
            throw new ResourceNotFoundException("Car", "id", carId);

        }

        Car car = optionalCar.get();

        List<Orders> allOrders = orderService.getAllOrders();
        long count = allOrders.stream().filter(order -> order.getCar() != null && order.getCar().getId() == carId).count();
        System.out.println(count);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("car id", carId);
        result.put("count", count);

        logger.info("Endpoint /admin/statistics/carrentalcount was used with carId " + carId);

        return result;


    }


    // TODO Exceptions + loggning
    @Override
    public Map<Integer, Long> getRentalDurationsByDays() {

        logger.info("Endpoint /admin/statistics/rentaldurations was used");

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

        logger.info("Endpoint /admin/statistics/averageordercost was used");

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

        logger.info("Endpoint /admin/statistics/revenuepercar was used");

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

        logger.info("Endpoint /admin/statistics/revenue was used");

        LocalDate start;
        LocalDate end;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            start = LocalDate.parse(startDate, formatter);
            end = LocalDate.parse(endDate, formatter);
        } catch (DateTimeParseException e) {
            throw new IncorrectInputException("Statistics", "date input", startDate + " and " + endDate, "yyyy-MM-dd", "Please use the correct date format.");
        }

        if (start.isAfter(end)) {
            System.out.println("Start date is after end date");
            throw new IncorrectInputException("Statistics", "Start-End Dates", ("Start:" + startDate + "->End:" + endDate), "YYYY-MM-DD", "Start date must be BEFORE end date.");
        }

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

        logger.info("Endpoint /admin/statistics/revenue was used with period " + startDate + " and " + endDate);

        return Map.of("Totala intäkter för perioden", totalRevenue);

    }


    // Metod för att kolla antal canceled orders under period
    @Override
    public Map<String, Object> getCanceledOrderCountByPeriod(String startDate, String endDate) {

        logger.info("Endpoint /admin/statistics/canceledordercountbyperiod was used");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start;
        LocalDate end;

        try {
            start = LocalDate.parse(startDate, formatter);
            end = LocalDate.parse(endDate, formatter);
        } catch (DateTimeParseException e) {
            logger.warn("Could not parse startDate {} endDate {} because wrong format was used.", startDate, endDate);
            throw new IncorrectInputException("Statistics", "date input", startDate + " and " + endDate, "yyyy-MM-dd", "");
        }

        if (start.isAfter(end)) {
            logger.warn("Invalid date because start date {} is after end date {}", startDate, endDate);
            throw new IncorrectInputException("Statistics", "Start-End Dates", startDate + " -> " + endDate, "YYYY-MM-DD", "Start date must be BEFORE end date.");
        }

        List<Orders> allOrders = orderService.getAllOrders();

        long canceledCount = allOrders.stream()
                .filter(order -> {
                    LocalDate orderDate = order.getHireStartDate().toLocalDate();
                    return order.isOrderCancelled() && (orderDate.isEqual(start) || orderDate.isAfter(start)) && (orderDate.isEqual(end) || orderDate.isBefore(end));
                })
                .count();

        double totalPriceOfCanceledOrders = allOrders.stream()
                .filter(order -> order.isOrderCancelled()
                        && !order.getHireStartDate().toLocalDate().isBefore(start)
                        && !order.getHireEndDate().toLocalDate().isAfter(end))
                .mapToDouble(Orders::getTotalPrice)
                .sum();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("start date", start);
        result.put("end date", end);
        result.put("cancelled orders", canceledCount);
        result.put("lost revenue", totalPriceOfCanceledOrders);

        logger.info("Endpoint /admin/statistics/cancelledorders was used with startDate {} and endDate {}", startDate, endDate);


        return result;
    }

}
