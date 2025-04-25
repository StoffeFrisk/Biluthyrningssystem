package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Address;
import com.example.Biluthyrningssystem.entities.Customer;
import com.example.Biluthyrningssystem.exceptions.ResourceNotFoundException;
import com.example.Biluthyrningssystem.repositories.AddressRepository;
import com.example.Biluthyrningssystem.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, AddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }


    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerByPersonnummer(String personnummer) {
        return customerRepository.findById(personnummer)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "personnummer", personnummer));
    }

    @Override
    public Customer addCustomer(Customer newCustomer) {
        Long addressId = newCustomer.getAddress().getId();

        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        newCustomer.setAddress(existingAddress);

        return customerRepository.save(newCustomer);
    }

    @Override
    public Customer updateCustomer(Customer customerToUpdate) {
        Customer existing = getCustomerByPersonnummer(customerToUpdate.getPersonnummer());

        existing.setFirstName(customerToUpdate.getFirstName());
        existing.setLastName(customerToUpdate.getLastName());
        existing.setEmail(customerToUpdate.getEmail());
        existing.setPhone(customerToUpdate.getPhone());

        Long addressId = customerToUpdate.getAddress().getId();
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        existing.setAddress(existingAddress);

        return customerRepository.save(existing);
    }

    @Override
    public void deleteCustomer(String id) {

        customerRepository.deleteById(id);
    }
}
