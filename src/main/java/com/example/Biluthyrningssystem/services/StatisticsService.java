// Niklas Einarsson

package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.dto.StatisticsDTO;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    List<StatisticsDTO> getStatistics();
    Map<String, Long> getMostRentedBrandForPeriod(String startDate, String endDate);
    Map<String, Object> getRentalCountByCar(Long carId);
    Map<Integer, Long> getRentalDurationsByDays();

    //TODO
    double getAverageCostPerOrder();
    double getTotalRevenuePerCar();
    double getTotalRevenueForPeriod(String startDate, String endDate);
}
