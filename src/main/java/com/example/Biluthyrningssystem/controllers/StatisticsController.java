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

}
