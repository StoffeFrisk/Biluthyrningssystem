package com.example.Biluthyrningssystem.repositories;

import com.example.Biluthyrningssystem.entities.Address;
import com.example.Biluthyrningssystem.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
class CustomerRepositoryTest { //Lynsey Fox

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    private static Customer customer;

    @BeforeEach
    void setUp() {
        Address address = new Address("4 Privet Drive", "WD25 7LR", "Little Whinging");
        address = addressRepository.save(address);
        customer = new Customer("19800731-6357","Harry", "Potter", address,"harry.potter@hotmail.com", "0790897821");
        customerRepository.save(customer);
    }

    @Test
    void existsByPersonnummerShouldReturnTrueTest() {

       boolean customerExists = customerRepository.existsByPersonnummer(customer.getPersonnummer());

       assertThat(customerExists).isTrue();
    }
    @Test
    void existsByPersonnummerShouldReturnFalseTest() {

        boolean customerExists = customerRepository.existsByPersonnummer("19780608-5298");

        assertThat(customerExists).isFalse();
    }
}