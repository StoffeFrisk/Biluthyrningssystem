// Niklas Einarsson

package com.example.Biluthyrningssystem.controllers;

import com.example.Biluthyrningssystem.dto.StatisticsDTO;
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

    @GetMapping("")
    public List<StatisticsDTO> getStatistics() {
        System.out.println("getStatistics");
        return statisticsService.getStatistics();
    }

    // Ändra så att alla bilar listas efter popularitet?
    @GetMapping("/statistics/mostrentedbrand/period/{startDate}/{endDate}")
    public ResponseEntity<Map<String, Object>> getMostRentedBrandForPeriod(@PathVariable String startDate, @PathVariable String endDate) {

        Map.Entry<String, Long> mostRented = statisticsService.getMostRentedBrandForPeriod(startDate, endDate)
                .entrySet()
                .stream()
                .findFirst()
                .orElse(Map.entry("Ingen data", 0L));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Det mest populära bilmärket under perioden var " + mostRented.getKey() +
                " med " + mostRented.getValue() + " uthyrningar.");
        response.put("brand", mostRented.getKey());
        response.put("rentals", mostRented.getValue());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/statistics/carrentalcount/{carId}")
    public ResponseEntity<Map<String, Object>> getRentalCountByCar(@PathVariable Long carId) {

        Map<String, Object> result = statisticsService.getRentalCountByCar(carId);
        Long count = (Long) result.get("count");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Bilen med ID " + carId + " har hyrts ut " + count + " gånger.");
        response.putAll(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/populardurations")
    public ResponseEntity<Map<String, Object>> getPopularDurations() {
        Map<Integer, Long> result = statisticsService.getRentalDurationsByDays();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", " De vanligaste längderna och hur ofta dom förekommer");

        Map<String, Object> details = new LinkedHashMap<>();
        result.forEach((days, frequency) -> {
            details.put("Dagar: " + days, "Bokningar: " + frequency);
        });

        response.put("längder", details);
        return ResponseEntity.ok(response);

    }


}
