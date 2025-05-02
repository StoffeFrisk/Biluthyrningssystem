// Niklas Einarsson

// Används inte nu

package com.example.Biluthyrningssystem.dto;

import com.example.Biluthyrningssystem.entities.Orders;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class StatisticsDTO {

    private String carBrand;
    private String carModel;
    private long orderCount;


    private double totalRevenue;
    private double averageRentalDuration;
    private double averageCostPerOrder;



    public StatisticsDTO(String carBrand, String carModel, long orderCount, double totalRevenue, double averageRentalDuration, double averageCostPerOrder) {
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
        this.averageRentalDuration = averageRentalDuration;
        this.averageCostPerOrder = averageCostPerOrder;
    }


    public StatisticsDTO(Orders orders, long orderCount) {
        this.carBrand = orders.getCar().getBrand();
        this.carModel = orders.getCar().getModel();
        this.orderCount = orderCount;
    }



    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getAverageRentalDuration() {
        return averageRentalDuration;
    }

    public void setAverageRentalDuration(double averageRentalDuration) {
        this.averageRentalDuration = averageRentalDuration;
    }

    public double getAverageCostPerOrder() {
        return averageCostPerOrder;
    }

    public void setAverageCostPerOrder(double averageCostPerOrder) {
        this.averageCostPerOrder = averageCostPerOrder;
    }
}
