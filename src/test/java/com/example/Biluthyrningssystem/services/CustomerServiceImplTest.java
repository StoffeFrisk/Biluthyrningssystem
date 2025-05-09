package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.dto.CustomerRegistrationDTO;
import com.example.Biluthyrningssystem.entities.Address;
import com.example.Biluthyrningssystem.entities.Customer;
import com.example.Biluthyrningssystem.repositories.AddressRepository;
import com.example.Biluthyrningssystem.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest { //Lynsey Fox

    private AddressRepository addressRepository;
    private InMemoryUserDetailsManager userDetailsManager;
    private CustomerRepository customerRepository;
    private CustomerServiceImpl customerService;
    private Address address;
    private Customer customer;

    @BeforeEach
    void setUp() {
        addressRepository = mock(AddressRepository.class);
        customerRepository = mock(CustomerRepository.class);
        userDetailsManager = mock(InMemoryUserDetailsManager.class);
        customerService = new CustomerServiceImpl(customerRepository, addressRepository,userDetailsManager);
        address = new Address("4 Privet Drive", "WD25 7LR", "Little Whinging");
        address.setId(1L);
        customer = new Customer("19800731-6357","Harry", "Potter", address,"harry.potter@hotmail.com", "0790897821");
    }

    @Test
    void addCustomerShouldSucceedWhenValid() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));
        when(customerRepository.save(customer)).thenReturn(customer);

        CustomerRegistrationDTO result = customerService.addCustomer(customer);

        assertThat(result).isNotNull();
        assertThat(result.getCustomer()).isEqualTo(customer);
        String expectedLoginDetails = "Login username: " + customer.getPersonnummer() + ", password: " + customer.getPersonnummer().substring(customer.getPersonnummer().length() - 4);
        assertThat(result.getLoginDetails()).isEqualTo(expectedLoginDetails);
        verify(customerRepository).save(customer);
        verify(addressRepository).findById(1L);
        verify(userDetailsManager).createUser(any());

    }

    @Test
    void addCustomerShouldThrowExceptionInvalidEmail() {
        customer.setEmail("harry.potter@hotmailcom");

        assertThatThrownBy(() -> customerService.addCustomer(customer))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid email");

    }

    @Test
    void updateCustomer() {
    }

    @Test
    void deleteCustomer() {

    }
}