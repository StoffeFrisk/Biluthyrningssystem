//Christoffer Frisk
package com.example.Biluthyrningssystem.controllers;

import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.services.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }
    
    @GetMapping("/cars")
    public ResponseEntity<List<Car>> getAvailableCars() {
        return ResponseEntity.ok(carService.getAvailableCars());
    }

    @GetMapping("/admin/cars")
    public ResponseEntity<List<Car>> getAvailableCarsAdmin() {
        return ResponseEntity.ok(carService.getAvailableCars());
    }

    @GetMapping("/admin/allcars")
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @PostMapping("/admin/addcar")
    public ResponseEntity<Car> addCar(@RequestBody Car car) {
        Car saved = carService.addCar(car);
        return ResponseEntity.status(201).body(saved);
    }

    @PutMapping("/admin/updatecar")
    public ResponseEntity<Car> updateCar(@RequestParam Long id, @RequestBody Car car) {
        Car updated = carService.updateCar(id, car);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/admin/removecar")
    public ResponseEntity<Void> removeCar(@RequestParam Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
