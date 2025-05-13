// Niklas Einarsson

package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.dto.CarRevenueDTO;
import com.example.Biluthyrningssystem.dto.CustomerRevenueDTO;
import com.example.Biluthyrningssystem.dto.RentalDurationDTO;
import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.entities.Customer;
import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.exceptions.DataNotFoundException;
import com.example.Biluthyrningssystem.exceptions.IncorrectInputException;
import com.example.Biluthyrningssystem.exceptions.ResourceNotFoundException;
import com.example.Biluthyrningssystem.repositories.CarRepository;
import com.example.Biluthyrningssystem.repositories.CustomerRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsServiceImpl.class);

    private final OrderService orderService;
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public StatisticsServiceImpl(OrderService orderService, CarRepository carRepository, CustomerRepository customerRepository) {
        this.orderService = orderService;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Map<String, Object> getStatistics() {

        Map<String, Object> statistics = new LinkedHashMap<>();

        Map<String, Object> orderCount2025 = getOrderCountForPeriod("2025-01-01", "2025-12-31");
        statistics.put("Orders 2025", orderCount2025.get("orders"));
        Map<String, Double> totalRevenue2025 = getTotalRevenueForPeriod("2025-01-01", "2025-12-31");
        statistics.put("Revenue 2025", totalRevenue2025.get("TotalRevenueForPeriod"));

        Map<String, Object> cancelledOrders = getCanceledOrderCountByPeriod("2025-01-01", "2025-12-31");
        statistics.put("Cancelled orders 2025", cancelledOrders.get("cancelledOrders"));
        statistics.put("Cancelled order percentage 2025", cancelledOrders.get("cancelledPercentage"));
        statistics.put("Lost revenue from cancelled orders 2025", cancelledOrders.get("lostRevenue"));

        Map<String, Double> revenuePerOrder = getAverageCostPerOrder();
        statistics.put("Average revenue of orders (All time)", revenuePerOrder.get("averageOrderPrice"));
        Map<String, Object> endpoints = new LinkedHashMap<>();

        endpoints.put("Available endpoints", List.of(
                "/statistics",
                "/statistics/mostrentedbrands/period/{startDate}/{endDate}",
                "/statistics/carrentalcount/{carId}",
                "/statistics/rentaldurations",
                "/statistics/averageorderprice",
                "/statistics/revenuepercar",
                "/statistics/revenue/period/{startDate}/{endDate}",
                "/statistics/cancelledorders/period/{startDate}/{endDate}",
                "/statistics/orders/period/{startDate}/{endDate}",
                "/statistics/customersbyrevenue"
        ));
        statistics.putAll(endpoints);

        logger.info("Endpoint /statistics was called");

        return statistics;
    }

    @Override
    public Map<String, Long> getMostRentedBrandForPeriod(String startDate, String endDate) {


        LocalDate[] dates = parseAndValidateDates(startDate, endDate, "/mostrentedbrands");
        LocalDate start = dates[0];
        LocalDate end = dates[1];

        List<Orders> allOrders = orderService.getOrdersOverlappingPeriod(start, end);

        Map<String, Long> brandCounts = allOrders.stream().filter(order -> !order.isOrderCancelled() && order.getCar() != null)
                .map(order -> order.getCar().getBrand())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        if (brandCounts.isEmpty()) {
            logger.warn("/mostrentedbrands Could not return any data because no brands were found during the given period " + startDate + " - " + endDate);
            throw new DataNotFoundException("/mostrentedbrands", startDate + " - " + endDate, "No brands were found during given period");
        }

        LinkedHashMap<String, Long> result = new LinkedHashMap<>();
        brandCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEachOrdered(entry -> result.put(entry.getKey(), entry.getValue()));

        logger.info("Endpoint /statistics/mostrentedbrand was called with startDate {} and endDate {}", startDate, endDate);

        return result;
    }

    @Override
    public Map<String, Object> getRentalCountByCar(Long carId) {

        Optional<Car> optionalCar = carRepository.findById(carId);
        if (optionalCar.isEmpty()) {
            logger.warn("/carrentalcount Could not find car with id {}", carId);
            throw new ResourceNotFoundException("Car", "id", carId);
        }

        Car car = optionalCar.get();

        List<Orders> allOrders = orderService.getAllOrders();
        if (allOrders.isEmpty()) {
            logger.warn("/carrentalcount/{} getAllOrders returned empty list with no orders", carId);
            throw new DataNotFoundException("/carrentalcount", carId.toString(), "No orders with valid data were found");
        }
        long count = allOrders.stream()
                .filter(order -> order.getCar() != null && !order.isOrderCancelled() && order.getCar().getId().equals(carId))
                .count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("carId", carId);
        result.put("brand", car.getBrand());
        result.put("model", car.getModel());
        result.put("registrationnumber", car.getRegistrationNumber());
        result.put("orderCount", count);

        logger.info("Endpoint /statistics/carrentalcount was called with carId {} and returned count {}", carId, count);

        return result;

    }

    @Override
    public List<RentalDurationDTO> getRentalDurationsByDays() {

        List<Orders> allOrders = orderService.getAllOrders();

        if (allOrders.isEmpty()) {
            logger.warn("/rentaldurations getAllOrders returned empty list with no orders");
            throw new DataNotFoundException("/rentaldurations", "", "No orders were found");
        }

        Map<Integer, Long> result = allOrders.stream()
                .filter(order -> order.getHireStartDate() != null && order.getHireEndDate() != null)
                .map(order -> {
                    long days = ChronoUnit.DAYS.between(order.getHireStartDate().toLocalDate(), order.getHireEndDate().toLocalDate());
                    return (int) days;
                })
                .collect(Collectors.groupingBy(days -> days, Collectors.counting()));

        if (result.isEmpty()) {
            logger.warn("/rentaldurations Result returned empty because no valid data was found");
            throw new DataNotFoundException("/rentaldurations", "", "No orders with valid data were found");
        }

        List<RentalDurationDTO> durations = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : result.entrySet()) {
            durations.add(new RentalDurationDTO(entry.getKey(), entry.getValue()));
        }
        durations.sort(Comparator.comparingInt(RentalDurationDTO::getDays));

        logger.info("Endpoint /rentaldurations was called and returned {} durations", result.size());

        return durations;
    }

    @Override
    public Map<String, Double> getAverageCostPerOrder() {


        List<Orders> allOrders = orderService.getAllOrders();

        if (allOrders.isEmpty()) {
            logger.warn("/averageorderprice getAllOrders returned empty list with no orders");
            throw new DataNotFoundException("/averageorderprice", "", "No orders were found");
        }

        List<Orders> ordersToCalculate = allOrders.stream()
                .filter(order -> order.getHireStartDate() != null && !order.isOrderCancelled())
                .toList();

        if (ordersToCalculate.isEmpty()) {
            logger.warn("/averageorderprice ordersToCalculate returned empty because no valid data was found");
            throw new DataNotFoundException("/averageorderprice", "", "No orders with valid data found");
        }

        double totalRevenue = ordersToCalculate.stream()
                .mapToDouble(Orders::getTotalPrice)
                .sum();

        double average = totalRevenue / ordersToCalculate.size();
        average = Math.round(average * 100) / 100.0;

        logger.info("Endpoint /statistics/averageorderprice was called and returned average of {}", average);

        return Map.of("averageOrderPrice", average);
    }

    @Override
    public List<CarRevenueDTO> getTotalRevenuePerCar() {

        List<Orders> allOrders = orderService.getAllOrders();

        if (allOrders.isEmpty()) {
            logger.warn("/revenuepercar getAllOrders returned empty list with no orders");
            throw new DataNotFoundException("/revenuepercar", "", "No orders were found");
        }

        Map<Long, CarRevenueDTO> carRevenueMap = new LinkedHashMap<>();
        for (Orders order : allOrders) {
            if (order.getCar() != null && !order.isOrderCancelled()) {
                Car car = order.getCar();
                Long carId = car.getId();
                double totalRevenue = order.getTotalPrice();

                carRevenueMap.compute(carId, (id, dto) -> {
                    if (dto == null) {
                        return new CarRevenueDTO(carId, car.getRegistrationNumber(), car.getModel(), car.getBrand(), Math.round(totalRevenue * 100) / 100.0);
                    } else {
                        double newRevenue = Math.round((dto.getTotalCarRevenue() + totalRevenue) * 100) / 100.0;
                        dto.setTotalCarRevenue(newRevenue);

                        return dto;
                    }
                });
            }
        }

        if (carRevenueMap.isEmpty()) {
            logger.warn("/carrevenuepercar getAllOrders returned empty list with no orders");
            throw new DataNotFoundException("/carrevenuepercar", "", "No orders with valid data were found");
        }

        logger.info("Endpoint /statistics/revenuepercar was called and returned revenue for {} cars", carRevenueMap.size());
        return new ArrayList<>(carRevenueMap.values());
    }


    @Override
    public Map<String, Double> getTotalRevenueForPeriod(String startDate, String endDate) {

        LocalDate[] dates = parseAndValidateDates(startDate, endDate, "/revenue");
        LocalDate start = dates[0];
        LocalDate end = dates[1];

        List<Orders> allOrders = orderService.getOrdersOverlappingPeriod(start, end);

        if (allOrders.isEmpty()) {
            logger.warn("/revenue allOrders returned empty list with no orders");
            throw new DataNotFoundException("/revenue No orders", "", "No orders were found");
        }

        List<Orders> filteredOrders = allOrders.stream().filter(order -> !order.isOrderCancelled()).toList();

        if (filteredOrders.isEmpty()) {
            logger.warn("/revenue No orders were found for startDate {} and endDate {} ", startDate, endDate);
            throw new DataNotFoundException("/revenue", startDate + " - " + endDate, "No valid orders were found during given period");
        }

        double totalRevenue = filteredOrders.stream().mapToDouble(Orders::getTotalPrice).sum();

        totalRevenue = Math.round(totalRevenue * 100) / 100.0;

        logger.info("Endpoint /statistics/revenue was called with startDate {} and endDate {} and returned {}", startDate, endDate, totalRevenue);

        return Map.of("TotalRevenueForPeriod", totalRevenue);

    }

    @Override
    public Map<String, Object> getCanceledOrderCountByPeriod(String startDate, String endDate) {

        LocalDate[] dates = parseAndValidateDates(startDate, endDate, "/revenue");
        LocalDate start = dates[0];
        LocalDate end = dates[1];

        List<Orders> allOrders = orderService.getOrdersOverlappingPeriod(start, end);

        if (allOrders.isEmpty()) {
            logger.warn("Endpoint statistics/cancelledorders allOrders returned empty list with no orders");
            throw new DataNotFoundException("/cancelledorders", "", "No orders were found");
        }

        long canceledCount = allOrders.stream().filter(Orders::isOrderCancelled).count();
        long totalOrderCount = allOrders.size();

        double totalPriceOfCanceledOrders = allOrders.stream().filter(Orders::isOrderCancelled).mapToDouble(Orders::getTotalPrice).sum();
        totalPriceOfCanceledOrders = Math.round(totalPriceOfCanceledOrders * 100) / 100.0;

        double cancelledPercentage = (double) canceledCount / totalOrderCount * 100;
        cancelledPercentage = Math.round(cancelledPercentage * 100) / 100.0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("startDate", start);
        result.put("endDate", end);
        result.put("cancelledOrders", canceledCount);
        result.put("totalOrderCount", totalOrderCount);
        result.put("cancelledPercentage", cancelledPercentage);
        result.put("lostRevenue", totalPriceOfCanceledOrders);

        logger.info("Endpoint /statistics/cancelledorders was called with startDate {} endDate {} and returned {} cancelled orders with revenue lost {} ", startDate, endDate, canceledCount, totalPriceOfCanceledOrders);

        return result;
    }

    @Override
    public Map<String, Object> getOrderCountForPeriod(String startDate, String endDate) {

        LocalDate[] dates = parseAndValidateDates(startDate, endDate, "/revenue");
        LocalDate start = dates[0];
        LocalDate end = dates[1];

        List<Orders> allOrders = orderService.getOrdersOverlappingPeriod(start, end);
        if (allOrders.isEmpty()) {
            logger.warn("Endpoint /statistics/orders allOrders returned empty list with no orders");
            throw new DataNotFoundException("/orders", "", "No orders were found");
        }

        List<Orders> activeOrders = allOrders.stream()
                .filter(order -> !order.isOrderCancelled())
                .toList();

        if (activeOrders.isEmpty()) {
            logger.warn("/statistics/orders activeOrders returned empty list with no orders");
            throw new DataNotFoundException("/statistics/orders", "", "No active orders were found during given period");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("startDate", start);
        result.put("endDate", end);
        result.put("orders", activeOrders.size());


        logger.info("Endpoint /statistics/orders was called with startDate {} endDate {} and returned order count {}", startDate, endDate, activeOrders.size());
        return result;
    }

    public List<CustomerRevenueDTO> getTopCustomersByRevenue() {
        List<Orders> allOrders = orderService.getAllOrders();

        if (allOrders.isEmpty()) {
            logger.warn("Endpoint /statistics/topcustomers allOrders returned empty list with no orders");
            throw new DataNotFoundException("/topcustomers", "", "No orders were found");
        }

        Map<String, Double> customersRevenueMap = new LinkedHashMap<>();

        for(Orders order : allOrders) {
            if(order.getCustomer() != null && !order.isOrderCancelled()) {
                String customerId = order.getCustomer().getPersonnummer();
                double revenue = order.getTotalPrice();

                customersRevenueMap.put(customerId, customersRevenueMap.getOrDefault(customerId,0.0) + revenue);
            }
        }

        if (customersRevenueMap.isEmpty()) {
            logger.warn("Endpoint /statistics/topcustomers returned empty map with no valid revenue data");
            throw new DataNotFoundException("/topcustomers", "", "No valid revenue data");
        }

        List<CustomerRevenueDTO> customerRevenueList = customersRevenueMap.entrySet().stream()
                .map(entry -> {
                    Customer customer = customerRepository.findById(entry.getKey())
                            .orElseThrow(() -> new ResourceNotFoundException("Customer", "personnummer", entry.getKey()));
                    String name = customer.getFirstName() + " " + customer.getLastName();
                    return new CustomerRevenueDTO(customer.getPersonnummer(), name, entry.getValue());
                })
                .sorted(Comparator.comparingDouble(CustomerRevenueDTO::getCustomerRevenue).reversed())
                .collect(Collectors.toList());

            return customerRevenueList;
    }


    private LocalDate[] parseAndValidateDates(String startDateString, String endDateString, String endpointName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate start = LocalDate.parse(startDateString, formatter);
            LocalDate end = LocalDate.parse(endDateString, formatter);

            if (start.isAfter(end)) {
                logger.warn("{} Invalid date range: start date {} is after end date {}", endpointName, startDateString, endDateString);
                throw new IncorrectInputException("Statistics", "Start-End Dates",
                        String.format("Start: %s -> End: %s", startDateString, endDateString),
                        "yyyy-MM-dd", "Start date must be BEFORE end date");
            }
            return new LocalDate[]{start, end};
        } catch (DateTimeParseException e) {
            logger.warn("{} Could not parse dates: {} - {}. Wrong format used", endpointName, startDateString, endDateString);
            throw new IncorrectInputException("Statistics", "date input",
                    startDateString + " and " + endDateString, "yyyy-MM-dd",
                    "Please use the correct date format");
        }
    }
}
