// Niklas Einarsson

package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.dto.CarRevenueDTO;
import com.example.Biluthyrningssystem.dto.RentalDurationDTO;
import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.exceptions.DataNotFoundException;
import com.example.Biluthyrningssystem.exceptions.ResourceNotFoundException;
import com.example.Biluthyrningssystem.repositories.CarRepository;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderService orderService;
    @Mock
    CarRepository carRepository;

    @Spy
    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    private List<Orders> mockOrders;
    Car testCar;

    @BeforeEach
    void setUp() {
        Orders orders1 = new Orders();
        Orders orders2 = new Orders();
        Orders orders3 = new Orders();
        Orders orders4 = new Orders();
        Orders orders5 = new Orders();
        Orders orders6 = new Orders();

        Car car1 = new Car();
        car1.setId(1L);
        car1.setBrand("Volvo");
        testCar = car1;
        Car car2 = new Car();
        car2.setId(2L);
        car2.setBrand("Saab");
        Car car3 = new Car();
        car3.setId(3L);
        car3.setBrand("Toyota");
        Car car4 = new Car();
        car4.setId(4L);
        car4.setBrand("Toyota");
        Car car5 = new Car();
        car5.setId(5L);
        car5.setBrand("Toyota");

        orders1.setCar(car1);
        orders1.setTotalPrice(1000.0);
        orders2.setCar(car2);
        orders2.setTotalPrice(2000.0);
        orders3.setCar(car3);
        orders3.setTotalPrice(3000.0);
        orders4.setCar(car4);
        orders4.setTotalPrice(0.0);
        orders5.setCar(car5);
        orders5.setTotalPrice(0.0);
        orders6.setCar(car1);
        orders6.setTotalPrice(0.0);

        orders1.setOrderCancelled(false);
        orders2.setOrderCancelled(false);
        orders3.setOrderCancelled(false);
        orders4.setOrderCancelled(false);
        orders5.setOrderCancelled(false);
        orders6.setOrderCancelled(false);

        orders1.setHireStartDate(Date.valueOf("2025-05-10"));
        orders1.setHireEndDate(Date.valueOf("2025-05-20"));
        orders2.setHireStartDate(Date.valueOf("2025-05-10"));
        orders2.setHireEndDate(Date.valueOf("2025-05-20"));
        orders3.setHireStartDate(Date.valueOf("2025-05-10"));
        orders3.setHireEndDate(Date.valueOf("2025-05-20"));

        mockOrders = List.of(orders1, orders2, orders3, orders4, orders5, orders6);

    }

    @Test
    void getStatisticsShouldNotReturnNullValues() {
        Map<String, Object> orderCount2025 = Map.of("orders", 500);
        doReturn(orderCount2025).when(statisticsService).getOrderCountForPeriod("2025-01-01", "2025-12-31");

        Map<String, Double> totalRevenue2025 = Map.of("TotalRevenueForPeriod", 1000000.0);
        doReturn(totalRevenue2025).when(statisticsService).getTotalRevenueForPeriod("2025-01-01", "2025-12-31");

        Map<String, Object> cancelledOrders = Map.of(
                "cancelledOrders", 100,
                "cancelledPercentage", 20.0,
                "lostRevenue", 200000.0
        );
        doReturn(cancelledOrders).when(statisticsService).getCanceledOrderCountByPeriod("2025-01-01", "2025-12-31");

        Map<String, Double> revenuePerOrder = Map.of("AverageOrderPrice", 2000.0);
        doReturn(revenuePerOrder).when(statisticsService).getAverageCostPerOrder();

        Map<String, Object> result = statisticsService.getStatistics();

        assertNotNull(result.get("Orders 2025"));
        assertNotNull(result.get("Revenue 2025"));
        assertNotNull(result.get("Cancelled orders 2025"));
        assertNotNull(result.get("Cancelled order percentage 2025"));
        assertNotNull(result.get("Lost revenue from cancelled orders 2025"));
        assertNotNull(result.get("Average revenue of orders (All time)"));
        assertNotNull(result.get("Available endpoints"));
    }




    @Test
    void getMostRentedBrandForPeriodShouldReturnCorrectOrderCounts() {
        when(orderService.getOrdersOverlappingPeriod(any(),any())).thenReturn(mockOrders);

        Map<String, Long> result = statisticsService.getMostRentedBrandForPeriod("2025-01-01", "2025-12-31");

        assertEquals(3, result.size());
        assertEquals(2, result.get("Volvo"));
        assertEquals(1, result.get("Saab"));
        assertEquals(3, result.get("Toyota"));
    }

    @Test
    void getMostRentedBrandsForPeriodShouldThrowExceptionWhenNoOrdersFound() {
        when(orderService.getOrdersOverlappingPeriod(any(),any())).thenReturn(Collections.emptyList());
        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () ->  statisticsService.getMostRentedBrandForPeriod("2025-01-01", "2025-12-31"));
        assertTrue(exception.getMessage().contains("No brands were found during given period"));
    }

    @Test
    void testMergeFunctionInToMap() {

        List<Map.Entry<String, Long>> entries = List.of(
                new AbstractMap.SimpleEntry<>("Volvo", 2L),
                new AbstractMap.SimpleEntry<>("Toyota", 2L),
                new AbstractMap.SimpleEntry<>("Volvo", 1L),
                new AbstractMap.SimpleEntry<>("Saab", 1L)
        );

        Map<String, Long> result = entries.stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));


        assertEquals(3, result.size());
        assertEquals(2L, result.get("Volvo"));
        assertEquals(2L, result.get("Toyota"));
        assertEquals(1L, result.get("Saab"));
    }






    @Test
    void getRentalCountByCarShouldReturnCorrectCountTwo() {
        when(carRepository.findById(testCar.getId())).thenReturn(Optional.of(testCar));
        when(orderService.getAllOrders()).thenReturn(mockOrders);

        Map<String, Object> result = statisticsService.getRentalCountByCar(testCar.getId());

        assertEquals(testCar.getId(), result.get("carId"));
        assertEquals(2L, result.get("orderCount"));
    }

    @Test
    void getRentalCountByCarShouldThrowExceptionWhenNoCarFound() {
        Long carId = 9000L;
        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> statisticsService.getRentalCountByCar(carId));

        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void getRentalCountByCarShouldThrowExceptionWhenNoOrdersFound() {
        Long carId = testCar.getId();
        when(carRepository.findById(carId)).thenReturn(Optional.of(testCar));
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> statisticsService.getRentalCountByCar(carId));

        assertTrue(exception.getMessage().contains("No orders with valid data were found"));
    }

    @Test
    void getRentalDurationsByDaysShouldCastExceptionBecauseNoValidDataFound() {

        Orders orders1 = new Orders();
        Orders orders2 = new Orders();
        orders1.setHireStartDate(null);
        orders1.setHireEndDate(null);
        orders2.setHireStartDate(null);
        orders2.setHireEndDate(null);

        List<Orders> orders = List.of(orders1, orders2);
        when(orderService.getAllOrders()).thenReturn(orders);

        DataNotFoundException exception = assertThrows(DataNotFoundException.class, () -> statisticsService.getRentalDurationsByDays());
        assertTrue(exception.getMessage().contains("No orders with valid data were found"));

    }

    @Test
    void getRentalDurationByDaysShouldCalculateDaysCorrectlyToTenDays() {

        Orders orders = mockOrders.get(0);

        when(orderService.getAllOrders()).thenReturn(List.of(orders));

        List<RentalDurationDTO> result = statisticsService.getRentalDurationsByDays();
        assertFalse(result.isEmpty());
        assertEquals(10,result.get(0).getDays());
    }



    @Test
    void getAverageCostPerOrderShouldCalculateAverageCostPerOrderTo2000() {

        when(orderService.getAllOrders()).thenReturn(mockOrders);

        Map<String, Double> result = statisticsService.getAverageCostPerOrder();

        assertEquals(2000.0, result.get("AverageOrderPrice"));
    }


    @Test
    void getAverageCostPerOrderShouldThrowExceptionWhenNoOrdersFound() {
        when(orderService.getAllOrders()).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> statisticsService.getAverageCostPerOrder());
    }


    @Test
    void getTotalRevenuePerCarShouldCalculateRevenueForVolvo1000AndSaab2000() {

        when(orderService.getAllOrders()).thenReturn(mockOrders);

        List<CarRevenueDTO> result = statisticsService.getTotalRevenuePerCar();

        assertEquals(5, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.getCarId() == 1L && dto.getTotalCarRevenue() == 1000.0), "Volvo should have 1000");
        assertTrue(result.stream().anyMatch(dto -> dto.getCarId() == 2L && dto.getTotalCarRevenue() == 2000.0), "Saab should have 2000");
    }

    @Test
    void getTotalRevenuePerCarShouldThrowExceptionWhenNoOrdersFound() {
        when(orderService.getAllOrders()).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> statisticsService.getTotalRevenuePerCar());
    }

    @Test
    void getTotalRevenueForPeriodShouldCorrectlyCalculateTotalRevenue6000() {

        when(orderService.getOrdersOverlappingPeriod(any(),any())).thenReturn(mockOrders);

        Map<String, Double> result = statisticsService.getTotalRevenueForPeriod("2025-05-10", "2025-05-20");
        assertEquals(6000.0, result.get("TotalRevenueForPeriod"));
    }

    @Test
    void getTotalRevenueForPeriodThrowsExceptionWhenAllOrdersCancelled() {
        Orders cancelledOrder = new Orders();
        cancelledOrder.setHireStartDate(Date.valueOf("2025-05-01"));
        cancelledOrder.setHireEndDate(Date.valueOf("2025-05-20"));
        cancelledOrder.setTotalPrice(1000.0);
        cancelledOrder.setOrderCancelled(true);

        when(orderService.getOrdersOverlappingPeriod(any(), any())).thenReturn(List.of(cancelledOrder));

        assertThrows(DataNotFoundException.class, () -> statisticsService.getTotalRevenueForPeriod("2025-05-01", "2025-05-20"));
    }

    @Test
    void getCanceledOrderCountByPeriodShouldReturnOneCanceledOrder() {
        Orders cancelledOrder = new Orders();
        cancelledOrder.setHireStartDate(Date.valueOf("2025-05-01"));
        cancelledOrder.setHireEndDate(Date.valueOf("2025-05-20"));
        cancelledOrder.setTotalPrice(1000.0);
        cancelledOrder.setOrderCancelled(true);


        List<Orders> testOrders = new ArrayList<>(mockOrders);
        testOrders.add(cancelledOrder);

        when(orderService.getOrdersOverlappingPeriod(any(), any())).thenReturn(testOrders);

        Map<String, Object> result = statisticsService.getCanceledOrderCountByPeriod("2025-05-01", "2025-05-20");

        assertEquals(1L, result.get("cancelledOrders"), "1 order should be cancelled");
        assertEquals(7L, result.get("totalOrderCount"), "totalOrderCount should be 7");
        assertEquals(14.29, result.get("cancelledPercentage"),"cancelledPercentage should be 14.29%");
        assertEquals(1000.0, result.get("lostRevenue"), "lostRevenue should be 1000");
}

    @Test
    void getCanceledOrderCountByPeriodShouldReturnNoCancelledOrders() {
        when(orderService.getOrdersOverlappingPeriod(any(), any())).thenReturn(mockOrders.stream().filter(o -> !o.isOrderCancelled()).toList());

        Map<String, Object> result = statisticsService.getCanceledOrderCountByPeriod("2025-05-01", "2025-05-20");

        assertEquals(0L, result.get("cancelledOrders"), "cancelledOrders should be 0");
        assertEquals(6L, result.get("totalOrderCount"), "totalOrderCount should be 6");
        assertEquals(0.0, result.get("cancelledPercentage"), "cancelledPercentage should be 0.0");
        assertEquals(0.0, result.get("lostRevenue"), "lostRevenue should be 0.0");
    }

    @Test
    void getOrderCountForPeriodShouldCalculateOrderCountCorrectlyToSix() {
        when(orderService.getOrdersOverlappingPeriod(any(), any())).thenReturn(mockOrders);

        Map<String, Object> result = statisticsService.getOrderCountForPeriod("2025-05-01", "2025-05-20");

        assertEquals(6, result.get("orders"), "Amount of orders should be 6");
    }

    @Test
    void getOrderCountForPeriodShouldThrowExceptionWhenNoOrdersFound() {
        when(orderService.getOrdersOverlappingPeriod(any(), any())).thenReturn(List.of());

        assertThrows(DataNotFoundException.class, () -> statisticsService.getOrderCountForPeriod("2025-05-01", "2025-05-20"));
    }
}