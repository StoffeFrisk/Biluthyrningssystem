// Niklas Einarsson

package com.example.Biluthyrningssystem.controllers;

import com.example.Biluthyrningssystem.dto.CarRevenueDTO;
import com.example.Biluthyrningssystem.dto.RentalDurationDTO;
import com.example.Biluthyrningssystem.services.StatisticsService;
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
        response.put("brandCounts", sortedBrands);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/statistics/carrentalcount/{carId}")
    public ResponseEntity<Map<String, Object>> getRentalCountByCar(@PathVariable Long carId) {

        Map<String, Object> result = statisticsService.getRentalCountByCar(carId);
        Long count = (Long) result.get("orderCount");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Car with ID " + carId + " has been booked " + count + " time(s).");
        response.putAll(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/rentaldurations")
    public ResponseEntity<Map<String, Object>> getPopularDurations() {

        List<RentalDurationDTO> durations = statisticsService.getRentalDurationsByDays();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Rental durations sorted by order count");
        response.put("durations", durations);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/averageorderprice")
    public ResponseEntity<Map<String, Double>> getAverageCost() {
        Map<String, Double> result = statisticsService.getAverageCostPerOrder();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/statistics/revenuepercar")
    public ResponseEntity<Map<String, Object>> getRevenuePerCar() {
        List<CarRevenueDTO> result = statisticsService.getTotalRevenuePerCar();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "All cars and their total revenue");
        response.put("revenuePerCar", result);

        return ResponseEntity.ok(response);
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

    @GetMapping("/statistics/orders/period/{startDate}/{endDate}")
    public ResponseEntity<Map<String, Object>> getOrderCount(@PathVariable String startDate, @PathVariable String endDate) {
        Map<String, Object> result = statisticsService.getOrderCountForPeriod(startDate, endDate);
        return ResponseEntity.ok(result);
    }

}
