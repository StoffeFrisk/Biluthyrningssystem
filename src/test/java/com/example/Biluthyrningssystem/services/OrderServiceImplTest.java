package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Customer;
import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.exceptions.ResourceNotFoundException;
import com.example.Biluthyrningssystem.exceptions.UnauthorisedRequestException;
import com.example.Biluthyrningssystem.repositories.CarRepository;
import com.example.Biluthyrningssystem.repositories.CustomerRepository;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@SqlGroup(
        @Sql(value = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
)
class OrderServiceImplTest {

    private OrderServiceImpl orderService;
    private OrderRepository orderRepository;
    private CarRepository carRepository;
    private CustomerRepository customerRepository;

    private static Orders order;
    private static String username;
    private static String secondUsername;

    @Autowired
    public OrderServiceImplTest(OrderServiceImpl orderService, OrderRepository orderRepository, CarRepository carRepository, CustomerRepository customerRepository){
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
        this.username = "19850101-1234";
        this.secondUsername = "19950505-7890";
    }

    @BeforeEach
    void beforeEach(){
        order = new Orders();
        order.setHireStartDate(Date.valueOf("2025-10-10"));
        order.setHireEndDate(Date.valueOf("2025-10-11"));
        order.setCar(carRepository.save(carRepository.findById(1L).get()));
    }

    @AfterEach
    void afterEach(){
        for (Orders order : orderRepository.findAll()){
            if (order.getId() > 7L){
                orderRepository.delete(order);
            }
        }
    }

    @Test
    void addOrderShouldReturnMatchingOrdersObject() {
        //When
        Orders addedOrder = orderService.addOrder(order,username);
        //Then
        assertThat(orderRepository.getOrdersById(addedOrder.getId()).equals(addedOrder));

        assertThat(order.getCustomer().equals(addedOrder.getCustomer()));
        assertEquals(998.0, addedOrder.getTotalPrice());
        assertFalse(addedOrder.isOrderCancelled());
    }

    @Test
    void addOrderShouldThrowUnauthorisedRequestException() {
        //Given
        order.setCustomer(customerRepository.findById(secondUsername).get());
        //When
        UnauthorisedRequestException response = assertThrows(UnauthorisedRequestException.class, ()-> orderService.addOrder(order,username));
        //Then
        assertThat(response.getMessage().equals("User ["+username+"] is not authorised to create new order for user: "+secondUsername+" - Users can only create orders for themselves"));
    }

    @Test
    void addOrderShouldThrowResourceNotFoundException() {
        //Given
        Customer nonCustomer = new Customer();
        nonCustomer.setPersonnummer("10");
        order.setCustomer(nonCustomer);
        //When
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, ()-> orderService.addOrder(order,username));
        //Then
        assertThat(response.getMessage().equals("Customer with ID '10' not found"));
    }

    @Test
    void cancelOrderShouldReturnTrue() {
        //Given
        Orders orderToBeCancelled = orderService.addOrder(order, username);
        assertThat(orderRepository.findById(orderToBeCancelled.getId()).equals(orderToBeCancelled));
        //When
        Orders cancelledOrder = orderService.cancelOrder(orderToBeCancelled, username);
        //Then
        assertTrue(cancelledOrder.isOrderCancelled());
    }

    @Test
    void cancelOrderShouldThrowUnauthorisedRequestException(){
        //Given
        Orders orderToBeCancelled = orderService.addOrder(order, username);
        assertThat(orderRepository.findById(orderToBeCancelled.getId()).equals(orderToBeCancelled));
        //When

        UnauthorisedRequestException response = assertThrows(UnauthorisedRequestException.class, () -> orderService.cancelOrder(orderToBeCancelled,secondUsername));
        //Then
        assertThat(response.getMessage().equals("User ["+secondUsername+"] is not authorised to cancel an order - Users can only edit their own orders"));
   }

    @Test
    void cancelOrderShouldThrowResourceNotFoundException(){
        //Given
        order.setId(12);
        //When
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, () -> orderService.cancelOrder(order,username));
        //Then
        assertThat(response.getMessage().equals("Order with ID '12' not found"));
    }

    @Test
    void getAllCustomerOrdersShouldEqual5() {
        //When
        List<Orders> expectedOrders = orderService.getAllCustomerOrders(secondUsername);
        //Then
        assertEquals(5,expectedOrders.size());
    }

    @Test
    void getActiveCustomerOrdersShouldEqual3() {
        //When
        List<Orders> expectedOrders = orderService.getActiveCustomerOrders(secondUsername);
        //Then
        assertEquals(3,expectedOrders.size());
    }

    @Test
    void getActiveOrdersShouldEqual2() {
        //When
        List<Orders> expectedOrders = orderService.getActiveOrders();
        //Then
        assertEquals(4, expectedOrders.size());
    }

    @Test
    void getAllOrdersShouldEqual3() {
        //When
        List<Orders> expectedOrders = orderService.getAllOrders();
        //Then
        assertEquals(7, expectedOrders.size());
    }

    @Test
    void deleteOrderByIdShouldRemoveOrderFrom() {
        //Given
        Orders orderToDelete = orderService.addOrder(order, username);
        assertThat(orderRepository.findById(orderToDelete.getId()).equals(orderToDelete));
        //When
        orderService.deleteOrderById(orderToDelete.getId());
        Optional<Orders> deletedOrder = orderRepository.findById(orderToDelete.getId());
        //Then
        assertThat(deletedOrder.isEmpty());
    }

    @Test
    void deleteOrderByIdShouldThrowResourceNotFoundException(){
        //When
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrderById(12L));
        //Then
        assertThat(response.getMessage().equals("Order with ID '12' not found"));
    }

    @Test
    void deleteOrdersBeforeDateShouldReturnListOfSize2And5OrdersShouldRemainInDatabase() {
        //When
        List<Long> deletedIDsList = orderService.deleteOrdersBeforeDate(Date.valueOf("2025-06-01"));
        //Then
        assertEquals(2, deletedIDsList.size());
        assertEquals(5, orderRepository.findAll().size());

    }
}