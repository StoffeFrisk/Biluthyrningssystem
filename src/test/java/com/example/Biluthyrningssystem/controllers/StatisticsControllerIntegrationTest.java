// Niklas Einarsson

package com.example.Biluthyrningssystem.controllers;

import static org.junit.jupiter.api.Assertions.*;

import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.exceptions.DataNotFoundException;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Map;
import java.util.Objects;

@SpringBootTest
@Transactional


class StatisticsControllerIntegrationTest {

        @Autowired
        private StatisticsController statisticsController;

        @Autowired
        private OrderRepository orderRepository;


    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();

        Orders order1 = new Orders();
        Orders order2 = new Orders();

        order1.setHireStartDate(Date.valueOf("2025-05-1"));
        order1.setHireEndDate(Date.valueOf("2025-05-10"));

        order2.setHireStartDate(Date.valueOf("2025-05-8"));
        order2.setHireEndDate(Date.valueOf("2025-05-20"));


        order1.setTotalPrice(3000.0);
        order2.setTotalPrice(4000.0);

        order1.setOrderCancelled(false);
        order2.setOrderCancelled(true);

        orderRepository.save(order1);
        orderRepository.save(order2);
    }

    @Test
    void getStatistics() {
    }

    @Test
    void getMostRentedBrandForPeriod() {
    }

    @Test
    void getRentalCountByCar() {
    }

    @Test
    void getPopularDurations() {
    }

    @Test
    void getAverageCost() {
    }

    @Test
    void getRevenuePerCar() {
    }

    @Test
    void getRevenueForPeriodShouldReturnTotalRevenueOf3000() {
        ResponseEntity<Map<String, Double>> response = statisticsController.getRevenueForPeriod("2025-05-01","2025-05-31");
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Response code should be 200");
        assertEquals(3000, Objects.requireNonNull(response.getBody(), "Response body should not be null").get("TotalRevenueForPeriod"), "Total revenue should be 3000");
    }

    @Test
    void getCancelledOrdersShouldReturnOneCancelledOrder() {
        ResponseEntity<Map<String, Object>> response = statisticsController.getCancelledOrders("2025-05-01","2025-05-31");
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Response code should be 200");
        assertEquals(1L, Objects.requireNonNull(response.getBody(), "Resone body should not be null").get("cancelledOrders"), "Should have 1 cancelled orders");
    }

    @Test
    void getOrderCountShouldReturnOneOrder() {
        ResponseEntity<Map<String, Object>> response = statisticsController.getOrderCount("2025-05-01","2025-05-31");
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Response code should be 200");
        assertEquals(1, Objects.requireNonNull(response.getBody(), "Response body should not be null").get("orders"), "Should have 1 orders");
    }

    @Test
    void getOrderCountShouldCastExceptionWhenNoOrdersFound() {
        orderRepository.deleteAll();

        assertThrows(DataNotFoundException.class, () -> statisticsController.getOrderCount("2025-05-01", "2025-05-31"), "DataNotFoundException should be thrown");

    }
}