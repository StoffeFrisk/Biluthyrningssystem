package com.example.Biluthyrningssystem.controllers;

import com.example.Biluthyrningssystem.dto.StatisticsDTO;
import com.example.Biluthyrningssystem.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")

public class StatisticsController {

    private final StatisticsService statisticsService;


    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public List<StatisticsDTO> getStatistics() {
        return statisticsService.getStatistics();
    }

    @GetMapping("/statistics/mostrentedbrand")
    public List<StatisticsDTO> getMostRentedCarsByBrand(String brand) {
        return statisticsService.getMostRentedCarsByBrand(brand);
    }

}
