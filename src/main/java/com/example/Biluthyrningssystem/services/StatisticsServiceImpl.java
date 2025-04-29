package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.dto.StatisticsDTO;
import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final OrderRepository orderRepository;

    @Autowired
    public StatisticsServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @Override
    public List<StatisticsDTO> getStatistics() {
        return List.of();
    }

    @Override
    public List<StatisticsDTO> getMostRentedBrandForPeriod(String startDate, String endDate) {
        return List.of();
    }

    @Override
    public List<StatisticsDTO> getRentalCountByCar(String brand, String startDate, String endDate) {
        return List.of();
    }

    @Override
    public double getAverageRentalDurationByDays() {
        return 0;
    }

    @Override
    public double getAverageCostPerOrder() {
        return 0;
    }

    @Override
    public double getTotalRevenuePerCar() {
        return 0;
    }

    @Override
    public double getTotalRevenueForPeriod(String startDate, String endDate) {
        return 0;
    }

    @Override
    public List<StatisticsDTO> getMostRentedCarsByBrand(String brand) {
    List<Orders> allOrders = orderRepository.findAll();
    int count = 0;
    for (Orders order : allOrders) {
        if (order.getCar().getBrand().equalsIgnoreCase(brand)) {
            count++;
        }
    }
    List<StatisticsDTO> result = new ArrayList<>();
    result.add(new StatisticsDTO(brand,null, count));
    return result;
    }

    @Override
    public List<StatisticsDTO> getMostRentedCarsForPeriod(String startDate, String endDate) {
        return List.of();
    }

}
