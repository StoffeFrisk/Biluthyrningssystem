package com.example.Biluthyrningssystem.controllers;

import static org.junit.jupiter.api.Assertions.*;

import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.exceptions.DataNotFoundException;
import com.example.Biluthyrningssystem.repositories.CustomerRepository;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import com.example.Biluthyrningssystem.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Map;

@SpringBootTest
@Transactional


class StatisticsControllerIntegrationTest {

        @Autowired
        private StatisticsController statisticsController;

        @Autowired
        private OrderService orderService;

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
    void getRevenueForPeriod() {
        ResponseEntity<Map<String, Double>> response = statisticsController.getRevenueForPeriod("2025-05-01","2025-05-31");
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Response code should be 200");
        assertEquals(3000, response.getBody().get("TotalRevenueForPeriod"), "Total revenue should be 3000");
    }

    @Test
    void getCancelledOrders() {
        ResponseEntity<Map<String, Object>> response = statisticsController.getCancelledOrders("2025-05-01","2025-05-31");
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Response code should be 200");
        assertEquals(1L, response.getBody().get("cancelledOrders"), "Should have 1 cancelled orders");
    }

    @Test
    void getOrderCount() {
        ResponseEntity<Map<String, Object>> response = statisticsController.getOrderCount("2025-05-01","2025-05-31");
        assertTrue(response.getStatusCode().is2xxSuccessful(), "Response code should be 200");
        assertEquals(1, response.getBody().get("orders"), "Should have 1 orders");
    }

    @Test
    void getOrderCountShouldCastExceptionWhenNoOrdersFound() {
        orderRepository.deleteAll();

        assertThrows(DataNotFoundException.class, () -> statisticsController.getOrderCount("2025-05-01", "2025-05-31"));

    }
}