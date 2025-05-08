// Niklas Einarsson

package com.example.Biluthyrningssystem.controllers;

import com.example.Biluthyrningssystem.dto.StatisticsDTO;
import com.example.Biluthyrningssystem.services.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")

public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = statisticsService.getStatistics();
        return ResponseEntity.ok(statistics);
    }


    @GetMapping("/statistics/mostrentedbrands/period/{startDate}/{endDate}")
    public ResponseEntity<Map<String, Object>> getMostRentedBrandForPeriod(@PathVariable String startDate, @PathVariable String endDate) {


        Map<String, Long> sortedBrands = statisticsService.getMostRentedBrandForPeriod(startDate, endDate);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "All car brands sorted by order count during period");
        response.put("data", sortedBrands);

        return ResponseEntity.ok(response);

    // Gammal kod för att lista endast den populäraste.

//        Map.Entry<String, Long> mostRented = statisticsService.getMostRentedBrandForPeriod(startDate, endDate)
//                .entrySet()
//                .stream()
//                .findFirst()
//                .orElse(Map.entry("Ingen data", 0L));
//
//        Map<String, Object> response = new LinkedHashMap<>();
//        response.put("message", "Det mest populära bilmärket under perioden var " + mostRented.getKey() +
//                " med " + mostRented.getValue() + " uthyrningar.");
//        response.put("brand", mostRented.getKey());
//        response.put("rentals", mostRented.getValue());
//
//        return ResponseEntity.ok(response);
    }


    @GetMapping("/statistics/carrentalcount/{carId}")
    public ResponseEntity<Map<String, Object>> getRentalCountByCar(@PathVariable Long carId) {

        Map<String, Object> result = statisticsService.getRentalCountByCar(carId);
        Long count = (Long) result.get("count");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Car with ID " + carId + " has been booked " + count + " time(s).");
        response.putAll(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/rentaldurations")
    public ResponseEntity<Map<String, Object>> getPopularDurations() {
        Map<Integer, Long> result = statisticsService.getRentalDurationsByDays();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "The most common rental durations.");

        Map<String, Object> details = new LinkedHashMap<>();
        result.forEach((days, frequency) -> {
            details.put("Days: " + days, "Bookings: " + frequency);
        });

        response.put("Durations", details);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/statistics/averageordercost")
    public ResponseEntity<Map<String, Double>> getAverageCost() {
        Map<String, Double> result = statisticsService.getAverageCostPerOrder();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/statistics/revenuepercar")
    public ResponseEntity<Map<Long, Double>> getRevenuePerCar() {
        Map<Long, Double> result = statisticsService.getTotalRevenuePerCar();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/statistics/revenue/period/{startDate}/{endDate}")
    public ResponseEntity<Map<String, Double>> getRevenueForPeriod(@PathVariable String startDate, @PathVariable String endDate) {
        return ResponseEntity.ok(statisticsService.getTotalRevenueForPeriod(startDate, endDate));

    }

    @GetMapping("/statistics/cancelledorders/period/{startDate}/{endDate}")
    public ResponseEntity<Map<String, Object>> getCancelledOrders(@PathVariable String startDate, @PathVariable String endDate) {
        Map<String, Object> result = statisticsService.getCanceledOrderCountByPeriod(startDate, endDate);
        return ResponseEntity.ok(result);
    }


}
