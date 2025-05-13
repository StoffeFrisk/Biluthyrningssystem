// Niklas Einarsson

package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.dto.CarRevenueDTO;
import com.example.Biluthyrningssystem.dto.CustomerRevenueDTO;
import com.example.Biluthyrningssystem.dto.RentalDurationDTO;

import java.util.List;
import java.util.Map;

public interface StatisticsService {

    Map<String, Object> getStatistics();
    Map<String, Long> getMostRentedBrandForPeriod(String startDate, String endDate);
    Map<String, Object> getRentalCountByCar(Long carId);
    List<RentalDurationDTO> getRentalDurationsByDays();
    Map<String, Double> getAverageCostPerOrder();
    List<CarRevenueDTO> getTotalRevenuePerCar();
//    Map<String, Object> getTotalRevenuePerCar();
    Map<String, Double> getTotalRevenueForPeriod(String startDate, String endDate);
    Map<String, Object> getCanceledOrderCountByPeriod(String startDate, String endDate);
    Map<String, Object> getOrderCountForPeriod(String startDate, String endDate);
    List<CustomerRevenueDTO> getTopCustomersByRevenue();
}
