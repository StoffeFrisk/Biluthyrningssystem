package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Address;
import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.entities.Customer;
import com.example.Biluthyrningssystem.entities.Orders;
import com.example.Biluthyrningssystem.repositories.CarRepository;
import com.example.Biluthyrningssystem.repositories.CustomerRepository;
import com.example.Biluthyrningssystem.repositories.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@SqlGroup(
        @Sql(value = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
)
class OrderServiceImplTest {

    private OrderServiceImpl orderService;
    private OrderRepository orderRepository;

    private static Orders order;
    private static Orders cancelledOrder;
    private static Orders secondCustomerOrder;
    private static Car car;
    private static Customer customer;
    private static Customer secondCustomer;
    private static Address address;

    @Autowired
    public OrderServiceImplTest(OrderServiceImpl orderService, OrderRepository orderRepository){
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CarRepository carRepositoryMock;

    @Mock
    private CustomerRepository customerRepositoryMock;

    @BeforeEach
    void beforeEach(){
        address = new Address("123 Test Street","88177", "Sundsvall");
        customer = new Customer("20011002-0123", "John", "Smith", address, "e@mail.com", "0712345678");
        secondCustomer = new Customer("20070128-1818", "Jane", "Smith", address, "mail@e.com", "0721436587");

        car = new Car("Polestar", "Polestar 2", "FBI 123", 800.00, false, false);
        order = new Orders(customerRepositoryMock.save(customer),carRepositoryMock.save(car), Date.valueOf("2025-05-10"), Date.valueOf("2025-05-11"), 800.00, false);
        cancelledOrder = new Orders(customerRepositoryMock.save(customer),carRepositoryMock.save(car), Date.valueOf("2025-05-10"), Date.valueOf("2025-05-11"), 800.00, true);
        secondCustomerOrder = new Orders(customerRepositoryMock.save(secondCustomer),carRepositoryMock.save(car), Date.valueOf("2025-05-10"), Date.valueOf("2025-05-11"), 800.00, false);
    }

    @AfterEach
    void afterEach(){
        List<Orders> usedOrders = orderService.getAllOrders();
        for (Orders order : usedOrders){
            orderRepository.delete(order);
        }
    }

    @Test
    void addOrder() {
    }

    @Test
    void cancelOrder() {
    }

    @Test

    void getAllCustomerOrdersShouldEqual2() {
        //orderRepository.save(new Orders(customerRepositoryMock.save(customer),carRepositoryMock.save(car), Date.valueOf("2025-05-10"), Date.valueOf("2025-05-11"), 800.00, false));

        //orderRepository.save(cancelledOrder);
        //When
        List<Orders> expectedOrders = orderService.getAllCustomerOrders("19850101-1234");

//        //Then
        assertEquals(2,expectedOrders.size());
    }

    @Test
    void getActiveCustomerOrders() {
    }

    @Test
    void getActiveOrdersShouldEqual2() {
        orderRepository.save(order);
        orderRepository.save(cancelledOrder);
        orderRepository.save(secondCustomerOrder);
        //When
        List<Orders> expectedOrders = orderService.getActiveOrders();
        //Then
        assertEquals(2, expectedOrders.size());
    }

    @Test
    void getAllOrdersShouldEqual3() {
        orderRepository.save(order);
        orderRepository.save(cancelledOrder);
        orderRepository.save(secondCustomerOrder);
        //When
        List<Orders> expectedOrders = orderService.getAllOrders();
        //Then
        assertEquals(3, expectedOrders.size());
    }

    @Test
    void deleteOrderById() {
    }

    @Test
    void deleteOrdersBeforeDate() {
    }
}