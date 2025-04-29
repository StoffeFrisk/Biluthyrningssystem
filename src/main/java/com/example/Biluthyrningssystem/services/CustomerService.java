package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Customer;

import java.util.List;

public interface CustomerService {

    public List<Customer> getAllCustomers();

    public Customer getCustomerByPersonnummer(String personnummer);

    public Customer addCustomer(Customer customer);

    public Customer updateCustomer(Customer newCustomer);

    public void deleteCustomer(String id);
}
