package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.exceptions.ResourceNotFoundException;
import com.example.Biluthyrningssystem.repositories.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceImplTest {

    @Mock                             private CarRepository repo;
    @InjectMocks                     private CarServiceImpl svc;

    private Car car1, car2;

    @BeforeEach
    void setUp() {
        car1 = new Car(1L, "Volvo", "V70", "ABC123", 499.0, false, false);
        car2 = new Car(2L, "Saab",  "9-3", "DEF456", 399.0, true,  false);
    }

    @Test
    void findAllAvailableCars() {
        when(repo.findAll()).thenReturn(List.of(car1, car2));

        List<Car> availableCars = svc.getAvailableCars();

        assertThat(availableCars).containsExactly(car1);
    }

    @Test
    void getAllCars() {
        when(repo.findAll()).thenReturn(List.of(car1, car2));

        List<Car> all = svc.getAllCars();

        assertThat(all).containsExactly(car1, car2);
    }

    @Test
    void getCarById() {
        when(repo.findById(1L)).thenReturn(Optional.of(car1));

        Car found = svc.getCarById(1L);

        assertThat(found).isSameAs(car1);
    }

    @Test
    void getCarByIdNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> svc.getCarById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Car with id '99' not found");
    }

    @Test
    void addCarAndReturnNewCar() {
        when(repo.save(car1)).thenReturn(car1);

        Car saved = svc.addCar(car1);

        assertThat(saved).isSameAs(car1);
        verify(repo).save(car1);
    }

    @Test
    void updateCarReturnsUpdatedCar() {
        Car updatedInfo = new Car(null, "Audi", "A4", "AUD123", 599.0, true, true);
        when(repo.findById(1L)).thenReturn(Optional.of(car1));
        when(repo.save(car1)).thenAnswer(inv -> inv.getArgument(0));

        Car result = svc.updateCar(1L, updatedInfo);

        assertThat(result.getBrand()).isEqualTo("Audi");
        assertThat(result.isBooked()).isTrue();
        verify(repo).findById(1L);
        verify(repo).save(car1);
    }

    @Test
    void updateCarMissingCarId() {
        when(repo.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> svc.updateCar(5L, car1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteCarDeletesCar() {
        when(repo.findById(1L)).thenReturn(Optional.of(car1));

        svc.deleteCar(1L);

        verify(repo).delete(car1);
    }

    @Test
    void deleteCarMissingCarId() {
        when(repo.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> svc.deleteCar(7L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
