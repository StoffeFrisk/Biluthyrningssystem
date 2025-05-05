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
        logger.info("Hämtar alla bilar från databasen.");
        List<Car> cars = carRepository.findAll();
        logger.info("Totalt {} bilar hämtade", cars.size());
        return cars;
    }

    @Override
    public List<Car> getAvailableCars() {
        logger.info("Hämtar tillgängliga bilar");
        List<Car> availableCars = carRepository.findAll()
                .stream()
                .filter(c->!c.isBooked() && !c.isInService())
                .collect(Collectors.toList());
        logger.info("{} bilar är tillgängliga", availableCars.size());
        return availableCars;
    }

    @Override
    public Car getCarById(Long id) {
        logger.info("Försöker hämta bil med id {}", id);

        try {
            Car car = carRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));
            logger.info("Bil hittad: {}", car);
            return car;

        } catch (ResourceNotFoundException ex) {
            logger.error("Bil hittades ej, id {}: {}", id, ex.getMessage());
            throw ex;
        }
    }
    @Override
    public Car addCar(Car car) {
        logger.info("Lägger till ny bil: {} {}", car.getBrand(), car.getModel());
        Car saved = carRepository.save(car);
        logger.info("Ny bil sparad med id {}", saved.getId());
        return saved;
    }

    @Override
    public Car updateCar(Long id, Car updatedCar) {
        logger.info("Försöker uppdatera bil med id {}", id);
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
            logger.info("Bil med id {} uppdaterad: {}", id, saved);
            return saved;
        } catch (ResourceNotFoundException ex) {
            logger.error("Uppdatering misslyckades för id {}: {}", id, ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void deleteCar(Long id) {
        logger.info("Försöker radera bil med id {}", id);
        try {
            Car existing = carRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));
            carRepository.delete(existing);
            logger.info("Bil med id {} raderad", id);
        } catch (ResourceNotFoundException ex) {
            logger.error("Radering misslyckades för id {}: {}", id, ex.getMessage());
            throw ex;
        }
    }
}

