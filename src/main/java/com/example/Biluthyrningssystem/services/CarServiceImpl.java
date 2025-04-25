//Christoffer Frisk

package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Override
    public Car getCarById(Long id) {
        return carRepository.findById(id).orElse(null);
    }

    @Override
    public Car addCar(Car car) {
        return carRepository.save(car);
    }

    @Override
    public Car updateCar(String id, Car updatedCar) {
        Long carId;
        try {
            carId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return null;
        }

        Optional<Car> existing = carRepository.findById(carId);
        if (existing.isPresent()) {
            Car car = existing.get();
            car.setBrand(updatedCar.getBrand());
            car.setModel(updatedCar.getModel());
            car.setPricePerDay(updatedCar.getPricePerDay());
            car.setBooked(updatedCar.isBooked());
            car.setInService(updatedCar.isInService());
            car.setRegistrationNumber(updatedCar.getRegistrationNumber());
            return carRepository.save(car);
        }

        return null;
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }
}
