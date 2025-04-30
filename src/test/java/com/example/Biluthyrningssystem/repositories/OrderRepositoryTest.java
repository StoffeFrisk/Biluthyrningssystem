package com.example.Biluthyrningssystem.repositories;

import com.example.Biluthyrningssystem.entities.Address;
import com.example.Biluthyrningssystem.entities.Car;
import com.example.Biluthyrningssystem.entities.Customer;
import com.example.Biluthyrningssystem.entities.Orders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.Date;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Mock
    private CarRepository carRepositoryMock;

    @Mock
    private AddressRepository addressRepositoryMock;

    @Mock
    private CustomerRepository customerRepositoryMock;

    private static Orders order;
    private static Car car;
    private static Address address;
    private static Customer customer;

    @BeforeEach
    void beforeEach(){
        car = new Car("Polestar", "Polestar 2", "FBI 123", 100.00, false, false);
        address = new Address("123 Nowhere Street", "82200", "Sundsvall");
        customer = new Customer("20000101-0212", "John", "Smith", address,"e@mail.com", "07123456789");
        order = new Orders(customerRepositoryMock.save(customer),carRepositoryMock.save(car) , Date.valueOf("2025-05-10"), Date.valueOf("2025-05-11"), 100.00, false);
    }

    @Test
    void findOrderByIDShouldReturnPresent() {
        orderRepository.save(order);

        Optional<Orders> expectedOrder = orderRepository.findById(1L);
        boolean orderFound = expectedOrder.isPresent();

        assertThat(orderFound).isTrue();
     }

    @Test
    void findOrderByIDShouldReturnFalse() {
        Optional<Orders> expectedOrder = orderRepository.findById(1L);
        boolean orderFound = expectedOrder.isPresent();

        assertThat(orderFound).isFalse();
    }
}