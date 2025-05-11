// Niklas Einarsson

package com.example.Biluthyrningssystem.dto;

public class CarRevenueDTO {

    private long carId;
    private String registrationNumber;
    private String model;
    private String brand;
    private double totalCarRevenue;

    public CarRevenueDTO(Long carId, String registrationNumber, String model, String brand, double totalCarRevenue) {
        this.carId = carId;
        this.registrationNumber = registrationNumber;
        this.model = model;
        this.brand = brand;
        this.totalCarRevenue = totalCarRevenue;
    }

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getTotalCarRevenue() {
        return totalCarRevenue;
    }

    public void setTotalCarRevenue(double totalCarRevenue) {
        this.totalCarRevenue = totalCarRevenue;
    }
}
