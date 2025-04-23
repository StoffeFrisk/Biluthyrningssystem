package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Customer;

import java.util.List;

public interface CustomerService {

    public List<Customer> getAllCustomers();

    public Customer getCustomerById(Long id);

    public Customer addCustomer(Customer customer);

    public Customer updateMember(String id, Customer newCustomer);

    public void deleteCustomer(String id);
}
