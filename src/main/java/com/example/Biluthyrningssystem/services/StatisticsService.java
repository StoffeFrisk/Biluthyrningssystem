package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.dto.StatisticsDTO;

import java.util.List;

public interface StatisticsService {

    List<StatisticsDTO> getStatistics();
    List<StatisticsDTO> getMostRentedBrandForPeriod(String startDate, String endDate);
    List<StatisticsDTO> getRentalCountByCar(String brand, String startDate, String endDate);
    double getAverageRentalDurationByDays();
    double getAverageCostPerOrder();
    double getTotalRevenuePerCar();
    double getTotalRevenueForPeriod(String startDate, String endDate);

    // Extra, ej obligatoriska
    List<StatisticsDTO> getMostRentedCarsByBrand(String brand);
    List<StatisticsDTO> getMostRentedCarsForPeriod(String startDate, String endDate);

}
