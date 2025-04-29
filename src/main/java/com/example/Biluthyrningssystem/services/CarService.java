// Christoffer Frisk

package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Car;

import java.util.List;

public interface CarService {

    public List<Car> getAvailableCars();

    public List<Car> getAllCars();

    public Car getCarById(Long id);

    public Car addCar(Car car);

    public Car updateCar(Long id, Car updateCar);

    public void deleteCar(Long id);
}
