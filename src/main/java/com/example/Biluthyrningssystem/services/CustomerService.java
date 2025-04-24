package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Customer;

import java.util.List;

public interface CustomerService {

    public List<Customer> getAllCustomers();

    public Customer getCustomerById(String id);

    public Customer addCustomer(Customer customer);

    public Customer updateCustomer(Customer newCustomer);

    public void deleteCustomer(String id);
}
