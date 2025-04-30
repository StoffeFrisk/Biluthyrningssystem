//Christoffer Frisk

package com.example.Biluthyrningssystem.entities;

import jakarta.persistence.*;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 15, nullable = false)
    private String brand;

    @Column(length = 10, nullable = false)
    private String model;

    @Column(length = 7, nullable = false)
    private String registrationNumber;

    @Column(length = 15, nullable = false)
    private double pricePerDay;

    private boolean booked;
    private boolean inService;

    public Car() {
    }

    public Car(String brand, String model, String registrationNumber, double pricePerDay, boolean booked, boolean inService) {
        this.brand = brand;
        this.model = model;
        this.registrationNumber = registrationNumber;
        this.pricePerDay = pricePerDay;
        this.booked = booked;
        this.inService = inService;
    }

    public Car(int id, String brand, String model, String registrationNumber, double pricePerDay, boolean booked, boolean inService) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.registrationNumber = registrationNumber;
        this.pricePerDay = pricePerDay;
        this.booked = booked;
        this.inService = inService;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public boolean isInService() {
        return inService;
    }

    public void setInService(boolean inService) {
        this.inService = inService;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", pricePerDay=" + pricePerDay +
                ", booked=" + booked +
                ", inService=" + inService +
                '}';
    }
}
