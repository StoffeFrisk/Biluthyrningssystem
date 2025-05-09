//Christoffer Frisk

package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.exceptions.ResourceNotFoundException;
import com.example.Biluthyrningssystem.repositories.CarRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        if (car.getBrand() == null || car.getBrand().isBlank()) {
            logger.error("Failed to add car: 'brand' canot be null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand cannot be null");
        }
        if (car.getModel() == null || car.getModel().isBlank()) {
            logger.error("Failed to add car: 'model' cannot be null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Model cannot be null");
        }
        if (car.getRegistrationNumber() == null || car.getRegistrationNumber().isBlank()) {
            logger.error("Failed to add car: 'registrationNumber' cannot be null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registreringsnummer cannot be null");
        }
        if (car.getPricePerDay() <= 0) {
            logger.error("Failed to add car: 'pricePerDay' cannot be null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pris per dag cannot be null");
        }

        logger.info("Adding new car: {} {}", car.getBrand(), car.getModel());
        try {
            Car saved = carRepository.save(car);
            logger.info("New car saved with id {}", saved.getId());
            return saved;
        } catch (Exception ex) {
            logger.error("Unexpected error when saving car {}: {}", car, ex.getMessage(), ex);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Cannot save car server error");
        }
    }

    @Override
    public Car updateCar(Long id, Car updatedCar) {
        if (id == null) {
            logger.error("Failed to update car: 'id' cannot be null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id cannot be null");
        }
        if (updatedCar.getBrand() == null || updatedCar.getBrand().isBlank()) {
            logger.error("Failed to update car {}: 'brand' cannot be null", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand cannot be null");
        }
        if (updatedCar.getModel() == null || updatedCar.getModel().isBlank()) {
            logger.error("Failed to update car {}: 'model' cannot be null", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Model cannot be null");
        }
        if (updatedCar.getRegistrationNumber() == null || updatedCar.getRegistrationNumber().isBlank()) {
            logger.error("Failed to update car {}: 'registrationNumber' cannot be null", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration number cannot be null");
        }
        if (updatedCar.getPricePerDay() <= 0) {
            logger.error("Failed to update car {}: 'pricePerDay' must be greater than 0", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price per day must be greater than 0");
        }

        logger.info("Attempting to update car with id {}", id);
        try {
            Car existing = carRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.error("Car not found for update, id={}", id);
                        return new ResourceNotFoundException("Car", "id", id);
                    });

            existing.setBrand(updatedCar.getBrand());
            existing.setModel(updatedCar.getModel());
            existing.setRegistrationNumber(updatedCar.getRegistrationNumber());
            existing.setPricePerDay(updatedCar.getPricePerDay());
            existing.setBooked(updatedCar.isBooked());
            existing.setInService(updatedCar.isInService());

            Car saved = carRepository.save(existing);
            logger.info("Car with id {} successfully updated: {}", id, saved);
            return saved;

        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (DataIntegrityViolationException dive) {
            logger.error("Failed to update car {}: duplicate registration number {}", id, updatedCar.getRegistrationNumber());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Registration number already exists");
        } catch (Exception ex) {
            logger.error("Unexpected error when updating car {}: {}", id, ex.getMessage(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot update car due to server error");
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

