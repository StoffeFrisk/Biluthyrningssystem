package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Customer;
import com.example.Biluthyrningssystem.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    @Override
    public List<Customer> getAllCustomers() {
        return List.of();
    }

    @Override
    public Customer getCustomerById(Long id) {
        return null;
    }

    @Override
    public Customer addCustomer(Customer customer) {
        return null;
    }

    @Override
    public Customer updateMember(String id, Customer newCustomer) {
        return null;
    }

    @Override
    public void deleteCustomer(String id) {

    }
}
