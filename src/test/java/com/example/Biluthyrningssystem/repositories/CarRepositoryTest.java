package com.example.Biluthyrningssystem.repositories;

import com.example.Biluthyrningssystem.entities.Car;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;


@DataJpaTest
class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    @Test
    void saveAndFindById() {
        Car car = new Car(null, "TestBrand", "TestModel", "ABC123", 123.0, false, false);

        Car savedCar = carRepository.saveAndFlush(car);
        Optional<Car> foundCar = carRepository.findById(savedCar.getId());

        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getBrand()).isEqualTo("TestBrand");
    }

    @Test
    void findAllReturnsSavedCars() {
        carRepository.deleteAll();
        Car a = new Car(null, "A", "One", "A111", 100, false, false);
        Car b = new Car(null, "B", "Two", "B222", 200, true, true);
        carRepository.saveAll(List.of(a, b));

        List<Car> allCars = carRepository.findAll();
        assertThat(allCars).hasSize(2)
                .extracting(Car::getRegistrationNumber)
                .containsExactlyInAnyOrder("A111", "B222");
    }

    @Test
    void availableFilterViaStream(){
        carRepository.deleteAll();
        Car free = new Car(null, "Free", "x", "A00", 100, false, false);
        Car booked = new Car(null, "Busy", "y", "B11", 110, true, false);
        Car service = new Car(null, "Svc", "z", "C33", 120, false, true);
        carRepository.saveAll(List.of(free, booked, service));

        List<Car> available = carRepository.findAll().stream()
                .filter(c -> !c.isBooked()&& !c.isInService())
                .toList();

        assertThat(available).hasSize(1)
                .first()
                .extracting(Car::getRegistrationNumber)
                .isEqualTo("A00");
    }
}

