// Niklas Einarsson

package com.example.Biluthyrningssystem.services;

import java.util.Map;

public interface StatisticsService {

    Map<String, Object> getStatistics();
    Map<String, Long> getMostRentedBrandForPeriod(String startDate, String endDate);
    Map<String, Object> getRentalCountByCar(Long carId);
    Map<Integer, Long> getRentalDurationsByDays();
    Map<String, Double> getAverageCostPerOrder();
    Map<Long, Double> getTotalRevenuePerCar();
    Map<String, Double> getTotalRevenueForPeriod(String startDate, String endDate);
    Map<String, Object> getCanceledOrderCountByPeriod(String startDate, String endDate);
    Map<String, Object> getOrderCountForPeriod(String startDate, String endDate);
}
