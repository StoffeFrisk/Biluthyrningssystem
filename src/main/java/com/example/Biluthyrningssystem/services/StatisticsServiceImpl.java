// Niklas Einarsson

package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.exceptions.DataNotFoundException;
import com.example.Biluthyrningssystem.exceptions.IncorrectInputException;
import com.example.Biluthyrningssystem.exceptions.ResourceNotFoundException;
import com.example.Biluthyrningssystem.repositories.CarRepository;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(StatisticsServiceImpl.class);
//    private static final Logger applicationLogger = LoggerFactory.getLogger("app");


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
            statistics.put("Revenue 2025", totalRevenue2025.get("Totala intäkter för perioden"));

        Map<String, Double> revenuePerOrder = getAverageCostPerOrder();
            statistics.put("Average revenue of orders", revenuePerOrder.get("Genomsnittlig kostnad per bokning"));

        Map<String, Object> cancelledOrders = getCanceledOrderCountByPeriod("2025-01-01", "2025-12-31");
            statistics.put("Cancelled orders 2025", cancelledOrders.get("cancelled orders"));
            statistics.put("Lost revenue from cancelled orders", cancelledOrders.get("lost revenue"));

        Map<String, Object> endpoints = new LinkedHashMap<>();

        endpoints.put("Available endpoints", List.of(
                "/statistics",
                "/statistics/mostrentedbrands/period/{startDate}/{endDate}",
                "/statistics/carrentalcount/{carId}",
                "/statistics/rentaldurations",
                "/statistics/averageordercost",
                "/statistics/revenuepercar",
                "/statistics/revenue/period/{startDate}/{endDate}",
                "/statistics/cancelledorders/period/{startDate}/{endDate}"
        ));
        statistics.putAll(endpoints);

        return statistics;
    }



    @Override
    public Map<String, Long> getMostRentedBrandForPeriod(String startDate, String endDate) {

        LocalDate start;
        LocalDate end;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            start = LocalDate.parse(startDate, formatter);
            end = LocalDate.parse(endDate, formatter);
        } catch (DateTimeParseException e) {
            logger.warn("/mostrentedbrands Could not parse date {} - {} because wrong format was used.", startDate,endDate);
            throw new IncorrectInputException("Statistics", "date input", startDate + " and " + endDate, "yyyy-MM-dd", "Please use the correct date format.");
        }

        if (start.isAfter(end)) {
            logger.warn("/mostrentedbrands Invalid date because start date {} is after end date {}", startDate, endDate);
            throw new IncorrectInputException("Statistics", "Start-End Dates", ("Start:" + startDate + "->End:" + endDate), "YYYY-MM-DD", "Start date must be BEFORE end date.");
        }

        List<Orders> allOrders = orderRepository.findAll();

        Map<String, Long> brandCounts = new HashMap<>();
        for (Orders order : allOrders) {
            LocalDate hireStart = order.getHireStartDate().toLocalDate();

            if ((hireStart.isEqual(start) || hireStart.isAfter(start)) &&
                    (hireStart.isEqual(end) || hireStart.isBefore(end))) {
                Car car = order.getCar();
                if (car != null) {
                    String brand = car.getBrand();
                    brandCounts.put(brand, brandCounts.getOrDefault(brand, 0L) + 1);
                }
            }
        }

        if(brandCounts.isEmpty()) {
            logger.warn("/mostrentedbrands Could not return any data because no brands were found during the given period " + startDate + " - " + endDate);
            throw new DataNotFoundException("/mostrentedbrands", startDate + " - " + endDate, "No brands were found during given period");
        }

        logger.info("Endpoint /admin/statistics/mostrentedbrand was used with startDate {} and endDate {}", startDate, endDate);

        return brandCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }


    @Override
    public Map<String, Object> getRentalCountByCar(Long carId) {

        Optional<Car> optionalCar = carRepository.findById(carId);
        if (optionalCar.isEmpty()) {
            logger.warn("/carrentalcount Could not find car with id {}.", carId);
            throw new ResourceNotFoundException("Car", "id", carId);

        }

        Car car = optionalCar.get();

        List<Orders> allOrders = orderService.getAllOrders();
        if(allOrders.isEmpty()) {
            logger.warn("/carrentalcount/{} getAllOrders returned empty list.", carId);
            throw new DataNotFoundException("/carrentalcount", carId.toString(), "No orders were found");
        }
        long count = allOrders.stream().filter(order -> order.getCar() != null && order.getCar().getId() == carId).count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("car id", carId);
        result.put("brand", car.getBrand());
        result.put("model", car.getModel());
        result.put("registrationnumber", car.getRegistrationNumber());
        result.put("order count", count);

        logger.info("Endpoint /admin/statistics/carrentalcount was used with carId {}.", carId);

        return result;

    }

    @Override
    public Map<Integer, Long> getRentalDurationsByDays() {

        List<Orders> allOrders = orderService.getAllOrders();

        if(allOrders.isEmpty()) {
            logger.warn("/rentaldurations getAllOrders returned empty list with no orders.");
            throw new DataNotFoundException("/rentaldurations","","No orders were found");
        }

        Map<Integer, Long> result = allOrders.stream()
                .filter(order -> order.getHireStartDate() != null && order.getHireEndDate() != null)
                .map(order -> {
                    long days = ChronoUnit.DAYS.between(order.getHireStartDate().toLocalDate(), order.getHireEndDate().toLocalDate());
                    return (int) days;
                })
                .collect(Collectors.groupingBy(days -> days, Collectors.counting()));

        if(result.isEmpty()) {
            logger.warn("/rentaldurations Result returned empty because no valid data was found.");
            throw new DataNotFoundException("/rentaldurations", "", "No orders with valid data found");
        }

        logger.info("Endpoint /rentaldurations was used and returned {} durations.", result.size());

            return result;
        }




    @Override
    public Map<String, Double> getAverageCostPerOrder() {


        List<Orders> allOrders = orderService.getAllOrders();

        if(allOrders.isEmpty()) {
            logger.warn("/averagecostperorder getAllOrders returned empty list with no orders.");
            throw new DataNotFoundException("/averagecostperorder","","No orders were found");
        }

        List<Orders> ordersToCalculate = allOrders.stream().filter(order -> order.getHireStartDate() != null).collect(Collectors.toList());

        if (ordersToCalculate.isEmpty()) {
            logger.warn("/averagecostperorder ordersToCalculate returned empty because no valid data was found.");
            throw new DataNotFoundException("/averagecostperorder","","No orders with valid data found");
        }

        double totalRevenue = ordersToCalculate.stream()
                .mapToDouble(Orders::getTotalPrice)
                .sum();

        double average = totalRevenue / ordersToCalculate.size();
        average = Math.round(average * 100) / 100.0;

        logger.info("Endpoint /admin/statistics/averageordercost was used and returned average of {}", average);

        return Map.of("Average order price", average);
    }


    @Override
    public Map<Long, Double> getTotalRevenuePerCar() {

        logger.info("Endpoint /admin/statistics/revenuepercar was used");

        List<Orders> allOrders = orderService.getAllOrders();

        if(allOrders.isEmpty()) {
        logger.warn("/revenuepercar getAllOrders returned empty list with no orders.");
        throw new DataNotFoundException("/revenuepercar","","No orders were found");
        }

        Map<Long, Double> revenuePerCar = new HashMap<>();

        for (Orders order : allOrders) {
            if (order.getCar() != null && !order.isOrderCancelled()) {
                Long carId = order.getCar().getId();
                double totalPrice = order.getTotalPrice();
                revenuePerCar.put(carId, revenuePerCar.getOrDefault(carId, 0.0) + totalPrice);

            }
        }

        if(revenuePerCar.isEmpty()) {
            logger.warn("/revenuepercar No valid orders were found");
            throw new DataNotFoundException("/revenuepercar","","No valid data was found in orders.");
        }

        logger.info("Endpoint /admin/statistics/revenuepercar was used and returned revenue of {} cars.", revenuePerCar.size());

        return revenuePerCar.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Math.round(e.getValue() * 100) / 100.0
                ));
    }



    @Override
    public Map<String, Double> getTotalRevenueForPeriod(String startDate, String endDate) {

        LocalDate start;
        LocalDate end;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            start = LocalDate.parse(startDate, formatter);
            end = LocalDate.parse(endDate, formatter);
        } catch (DateTimeParseException e) {
            logger.warn("/revenue Could not parse date {} - {} because wrong format was used.", startDate, endDate);
            throw new IncorrectInputException("Statistics", "date input", startDate + " and " + endDate, "yyyy-MM-dd", "");
        }

        if (start.isAfter(end)) {
            logger.warn("/revenue Invalid date because start date {} is after end date {}", startDate, endDate);
            throw new IncorrectInputException("Statistics", "Start-End Dates", ("Start:" + startDate + "->End:" + endDate), "YYYY-MM-DD", "Start date must be BEFORE end date.");
        }

        List<Orders> allOrders = orderService.getAllOrders();

        if (allOrders.isEmpty()) {
            logger.warn("/revenue getAllOrders returned empty list with no orders.");
            throw new DataNotFoundException("/revenue No orders", "", "No orders were found");
        }


        List<Orders> filteredOrders = allOrders.stream()
                .filter(order -> {
                    LocalDate hireStart = order.getHireStartDate().toLocalDate();
                    return !order.isOrderCancelled() && (hireStart.isEqual(start) || hireStart.isAfter(start)) &&
                            (hireStart.isEqual(end) || hireStart.isBefore(end));
                })
                .toList();

        if (filteredOrders.isEmpty()) {
            logger.warn("/revenue No orders were found for startDate {} and endDate {} ", startDate, endDate);
            throw new DataNotFoundException("/revenue", startDate + " - " + endDate, "No valid orders were found during given period");
        }


        double totalRevenue = filteredOrders.stream().mapToDouble(Orders::getTotalPrice).sum();

        totalRevenue = Math.round(totalRevenue * 100) / 100.0;

        logger.info("Endpoint /admin/statistics/revenue was used with startDate {} and endDate {} and returned {}", startDate, endDate, totalRevenue);

        return Map.of("Total revenue for period", totalRevenue);

    }


    @Override
    public Map<String, Object> getCanceledOrderCountByPeriod(String startDate, String endDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start;
        LocalDate end;

        try {
            start = LocalDate.parse(startDate, formatter);
            end = LocalDate.parse(endDate, formatter);
        } catch (DateTimeParseException e) {
            logger.warn("/cancelledorders Could not parse date {} - {} because wrong format was used.", startDate, endDate);
            throw new IncorrectInputException("Statistics", "date input", startDate + " and " + endDate, "yyyy-MM-dd", "");
        }

        if (start.isAfter(end)) {
            logger.warn("Invalid date because start date {} is after end date {}", startDate, endDate);
            throw new IncorrectInputException("Statistics", "Start-End Dates", startDate + " -> " + endDate, "YYYY-MM-DD", "Start date must be BEFORE end date.");
        }

        List<Orders> allOrders = orderService.getAllOrders();

        if (allOrders.isEmpty()) {
            logger.warn("/cancelledorders getAllOrders returned empty list with no orders.");
            throw new DataNotFoundException("/cancelledorders", "", "No orders were found" );
        }

        long canceledCount = allOrders.stream()
                .filter(order -> {
                    LocalDate orderDate = order.getHireStartDate().toLocalDate();
                    return order.isOrderCancelled()
                            && (orderDate.isEqual(start) || orderDate.isAfter(start))
                            && (orderDate.isEqual(end) || orderDate.isBefore(end));
                })
                .count();


        double totalPriceOfCanceledOrders = allOrders.stream()
                .filter(order -> {
                    LocalDate hireStart = order.getHireStartDate().toLocalDate();
                    LocalDate hireEnd = order.getHireEndDate().toLocalDate();
                    return order.isOrderCancelled()
                            && (hireStart.isEqual(start) || hireStart.isAfter(start))
                            && (hireEnd.isEqual(end) || hireEnd.isBefore(end));
                })
                .mapToDouble(Orders::getTotalPrice)
                .sum();


        totalPriceOfCanceledOrders = Math.round(totalPriceOfCanceledOrders * 100) / 100.0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("start date", start);
        result.put("end date", end);
        result.put("cancelled orders", canceledCount);
        result.put("lost revenue", totalPriceOfCanceledOrders);

        logger.info("Endpoint /admin/statistics/cancelledorders was used with startDate {} endDate {} and returned {} cancelled orders with revenue lost {} ", startDate, endDate,canceledCount, totalPriceOfCanceledOrders );


        return result;
    }

}
