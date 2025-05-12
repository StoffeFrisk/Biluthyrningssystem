package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.entities.Customer;
import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.exceptions.*;
import com.example.Biluthyrningssystem.repositories.CarRepository;
import com.example.Biluthyrningssystem.repositories.CustomerRepository;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

//BP
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@SqlGroup(
        @Sql(value = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
)
@Transactional
@Rollback
class OrderServiceImplTest {

    private OrderServiceImpl orderService;
    private OrderRepository orderRepository;
    private CarRepository carRepository;
    private CustomerRepository customerRepository;

    private static Orders order;
    private String username;
    private String secondUsername;

    @Autowired
    public OrderServiceImplTest(OrderServiceImpl orderService, OrderRepository orderRepository, CarRepository carRepository, CustomerRepository customerRepository){
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
    }

    @BeforeEach
    void beforeEach(){
        this.username = "19850101-1234";
        this.secondUsername = "19950505-7890";

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
        Optional<Orders> completedOrder = orderRepository.getOrdersById(addedOrder.getId());
        assertEquals(addedOrder, completedOrder.get());
        assertEquals(998.0, addedOrder.getTotalPrice());
        assertFalse(addedOrder.isOrderCancelled());
    }

    @Test
    void addOrderWithCustomerContainingOnlyIDShouldReturnMatchingOrdersObject() {
        //When
        Customer emptyCustomer = new Customer();
        emptyCustomer.setPersonnummer(username);
        order.setCustomer(emptyCustomer);
        Orders addedOrder = orderService.addOrder(order,username);
        //Then
        Optional<Orders> completedOrder = orderRepository.getOrdersById(addedOrder.getId());
        assertEquals(addedOrder, completedOrder.get());
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
        assertEquals("User ["+username+"] is not authorised to create new order for user: "+secondUsername+" - Users can only create orders for themselves",response.getMessage());
    }

    @Test
    void addOrderWithNonExistentCustomerShouldThrowResourceNotFoundException() {
        //Given
        Customer nonCustomer = new Customer();
        nonCustomer.setPersonnummer("10");
        order.setCustomer(nonCustomer);
        //When
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, ()-> orderService.addOrder(order,username));
        //Then
        assertEquals("Customer with ID '10' not found",response.getMessage());
    }

    @Test
    void cancelOrderShouldReturnTrue() {
        //Given
        Orders orderToBeCancelled = orderService.addOrder(order, username);
        assertEquals(orderToBeCancelled, orderRepository.findById(orderToBeCancelled.getId()).get());
        //When
        Orders cancelledOrder = orderService.cancelOrder(orderToBeCancelled, username);
        //Then
        assertTrue(cancelledOrder.isOrderCancelled());
    }

    @Test
    void cancelOrderShouldThrowUnauthorisedRequestException(){
        //Given
        Orders orderToBeCancelled = orderService.addOrder(order, username);
        assertEquals(orderToBeCancelled, orderRepository.findById(orderToBeCancelled.getId()).get());
        //When

        UnauthorisedRequestException response = assertThrows(UnauthorisedRequestException.class, () -> orderService.cancelOrder(orderToBeCancelled,secondUsername));
        //Then
        assertEquals("User ["+secondUsername+"] is not authorised to cancel an order - Users can only edit their own orders",response.getMessage());
   }

    @Test
    void cancelOrderShouldThrowResourceNotFoundException(){
        //Given
        order.setId(12);
        //When
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, () -> orderService.cancelOrder(order,username));
        //Then
        assertEquals("Order with ID '12' not found",response.getMessage());
    }

    @Test
    void cancelOrderShouldThrowRepeatRequestException() {
        //Given
        Orders orderToBeCancelled = orderService.addOrder(order, username);
        assertEquals(orderToBeCancelled, orderRepository.findById(orderToBeCancelled.getId()).get());
        //When
        Orders cancelledOrder = orderService.cancelOrder(orderToBeCancelled, username);
        RepeatRequestException response = assertThrows(RepeatRequestException.class, () -> orderService.cancelOrder(orderToBeCancelled, username));
        //Then
        assertEquals("Order with ID '"+cancelledOrder.getId()+"' : Order has already been cancelled.", response.getMessage());
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
        List<Orders> expectedOrders = orderService.getActiveCustomerOrders(username);
        //Then
        assertEquals(1,expectedOrders.size());
    }

    @Test
    void getActiveOrdersShouldEqual4() {
        //When
        List<Orders> expectedOrders = orderService.getActiveOrders();
        //Then
        assertEquals(4, expectedOrders.size());
    }

    @Test
    void getAllOrdersShouldEqual8() {
        //When
        List<Orders> expectedOrders = orderService.getAllOrders();
        //Then
        assertEquals(8, expectedOrders.size());
    }

    @Test
    void deleteOrderByIdShouldRemoveOrderFrom() {
        //Given
        Orders orderToDelete = orderService.addOrder(order, username);
        assertEquals(orderToDelete, orderRepository.findById(orderToDelete.getId()).get());
        //When
        orderService.deleteOrderById(orderToDelete.getId());
        Optional<Orders> deletedOrder = orderRepository.findById(orderToDelete.getId());
        //Then
        assertTrue(deletedOrder.isEmpty());
    }

    @Test
    void deleteOrderByIdShouldThrowResourceNotFoundException(){
        //When
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrderById(12L));
        //Then
        assertEquals("Order with ID '12' not found",response.getMessage());
    }

    @Test
    void deleteOrdersBeforeDateShouldReturnListOfSize2And5OrdersShouldRemainInDatabase() {
        //When
        List<Long> deletedIDsList = orderService.deleteOrdersBeforeDate(Date.valueOf("2025-06-01"));
        //Then
        assertEquals(2, deletedIDsList.size());
        assertEquals(6, orderRepository.findAll().size());

    }



    @Test
    void addOrderWithoutCarShouldThrowResourceNotFoundException() {
        //Given
        order.setCar(null);
        //When
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, ()-> orderService.addOrder(order,username));
        //Then
        assertEquals("Car with ID 'null' not found", response.getMessage());
    }

    @Test
    void addOrderWithNonExistentCarShouldThrowResourceNotFoundException() {
        //Given
        Car falseCar = new Car();
        falseCar.setId(12L);
        order.setCar(falseCar);
        //When
        ResourceNotFoundException response = assertThrows(ResourceNotFoundException.class, ()-> orderService.addOrder(order,username));
        //Then
        assertEquals("Car with ID '12' not found", response.getMessage());
    }

    @Test
    void addOrderWithoutStartDateShouldThrowIncorrectInputException() {
        //Given
        order.setHireStartDate(null);
        //When
        IncorrectInputException response = assertThrows(IncorrectInputException.class, ()-> orderService.addOrder(order,username));
        //Then
        assertEquals("Order attribute - Hire Start Date, with value null, is formatted incorrectly. Enter data with the following format YYYY-MM-DD. Start date must be BEFORE end date.",response.getMessage());
    }

    @Test
    void addOrderWithoutEndDateShouldThrowIncorrectInputException() {
        //Given
        order.setHireEndDate(null);
        //When
        IncorrectInputException response = assertThrows(IncorrectInputException.class, ()-> orderService.addOrder(order,username));
        //Then
        assertEquals("Order attribute - Hire End Date, with value null, is formatted incorrectly. Enter data with the following format YYYY-MM-DD. End date must be AfTER start date.",response.getMessage());
    }

    @Test
    void addOrderWithStartDateBeforeTodaysDateShouldThrowIncorrectInputException() {
        //Given
        order.setHireStartDate(Date.valueOf("2024-10-10"));
        order.setHireEndDate(Date.valueOf("2025-10-01"));
        //When
        IncorrectInputException response = assertThrows(IncorrectInputException.class, ()-> orderService.addOrder(order,username));
        //Then
        assertEquals("Order attribute - Hire Start Date, with value 2024-10-10, is formatted incorrectly. Enter data with the following format YYYY-MM-DD. Start date must be ON|AFTER today's date.",response.getMessage());
    }

    @Test
    void addOrderWithEndDateBeforeStartDateShouldThrowIncorrectInputException() {
        //Given
        order.setHireStartDate(Date.valueOf("2025-10-10"));
        order.setHireEndDate(Date.valueOf("2025-10-01"));
        //When
        IncorrectInputException response = assertThrows(IncorrectInputException.class, ()-> orderService.addOrder(order,username));
        //Then
        assertEquals("Order attribute - Hire Start-End Dates, with value Start:2025-10-10->End:2025-10-01, is formatted incorrectly. Enter data with the following format YYYY-MM-DD. Start date must be BEFORE end date.",response.getMessage());
    }

    @Test
    void addOrderWithIncorrectTotalPriceShouldThrowIncorrectCalculationException() {
        //Given
        order.setTotalPrice(103.0);
        //When
        IncorrectCalculationException response = assertThrows(IncorrectCalculationException.class, ()-> orderService.addOrder(order,username));
        //Then
        assertEquals("Order with Total Price [103.0] does not match calculated value [998.0]. Enter calculated value or leave field empty to be automatically calculated.",response.getMessage());
    }


    @Test
    void addOrderThatIsAlreadyCancelledShouldThrowIncorrectInputEsception() {
        //Given
        order.setOrderCancelled(true);
        //When
        IncorrectInputException response = assertThrows(IncorrectInputException.class, ()-> orderService.addOrder(order,username));
        //Then
        assertEquals("Order attribute - Order Cancelled, with value true, is formatted incorrectly. Enter data with the following format false. You cannot create a cancelled order.",response.getMessage());
    }

    @Test
    void addOrderWithNullCancelStatusShouldReturnMatchingOrdersObject() {
        //When
        order.setOrderCancelled(Boolean.valueOf(null));
        Orders addedOrder = orderService.addOrder(order,username);
        //Then
        Optional<Orders> completedOrder = orderRepository.getOrdersById(addedOrder.getId());
        assertEquals(addedOrder, completedOrder.get());
        assertEquals(998.0, addedOrder.getTotalPrice());
        assertFalse(addedOrder.isOrderCancelled());
    }

    @Test
    void addOrderWithNullTotalPriceShouldReturnMatchingOrdersObject() {
        //When
        order.setTotalPrice(null);
        Orders addedOrder = orderService.addOrder(order,username);
        //Then
        Optional<Orders> completedOrder = orderRepository.getOrdersById(addedOrder.getId());
        assertEquals(addedOrder, completedOrder.get());
        assertEquals(998.0, addedOrder.getTotalPrice());
        assertFalse(addedOrder.isOrderCancelled());
    }

    @Test
    void addOrderWithTotalPrice0ShouldReturnMatchingOrdersObject() {
        //When
        order.setTotalPrice(Double.valueOf(0));
        Orders addedOrder = orderService.addOrder(order,username);
        //Then
        Optional<Orders> completedOrder = orderRepository.getOrdersById(addedOrder.getId());
        assertEquals(addedOrder, completedOrder.get());
        assertEquals(998.0, addedOrder.getTotalPrice());
        assertFalse(addedOrder.isOrderCancelled());
    }

}