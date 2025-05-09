//Christoffer Frisk

package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.exceptions.ResourceNotFoundException;
import com.example.Biluthyrningssystem.repositories.CarRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private static final Logger logger = LogManager.getLogger(CarServiceImpl.class);
    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<Car> getAllCars() {
        logger.info("Getting all cars from database.");
        List<Car> cars = carRepository.findAll();
        logger.info("A total of {} cars shown", cars.size());
        return cars;
    }

    @Override
    public List<Car> getAvailableCars() {
        logger.info("Getting available cars from database.");
        List<Car> availableCars = carRepository.findAll()
                .stream()
                .filter(c->!c.isBooked() && !c.isInService())
                .collect(Collectors.toList());
        logger.info("A total of {} cars available", availableCars.size());
        return availableCars;
    }

    @Override
    public Car getCarById(Long id) {
        logger.info("Attempting to get car with id: {}", id);

        try {
            Car car = carRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));
            logger.info("Car found: {}", car);
            return car;

        } catch (ResourceNotFoundException ex) {
            logger.error("Car not found, id {}: {}", id, ex.getMessage());
            throw ex;
        }
    }
    @Override
    public Car addCar(Car car) {
        logger.info("Adding new car: {} {}", car.getBrand(), car.getModel());
        Car saved = carRepository.save(car);
        logger.info("New car saved with id {}", saved.getId());
        return saved;
    }

    @Override
    public Car updateCar(Long id, Car updatedCar) {
        logger.info("Attempting to update car with id {}", id);
        try {
            Car existing = carRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));

            existing.setBrand(updatedCar.getBrand());
            existing.setModel(updatedCar.getModel());
            existing.setRegistrationNumber(updatedCar.getRegistrationNumber());
            existing.setPricePerDay(updatedCar.getPricePerDay());
            existing.setBooked(updatedCar.isBooked());
            existing.setInService(updatedCar.isInService());

            Car saved = carRepository.save(existing);
            logger.info("Car with id {} updated: {}", id, saved);
            return saved;
        } catch (ResourceNotFoundException ex) {
            logger.error("Failed to update car with id {}: {}", id, ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void deleteCar(Long id) {
        logger.info("Attempting to delete car with id: {}", id);
        try {
            Car existing = carRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));
            carRepository.delete(existing);
            logger.info("Car with id {} deleted", id);
        } catch (ResourceNotFoundException ex) {
            logger.error("Failed deletion for id {}: {}", id, ex.getMessage());
            throw ex;
        }
    }
}

