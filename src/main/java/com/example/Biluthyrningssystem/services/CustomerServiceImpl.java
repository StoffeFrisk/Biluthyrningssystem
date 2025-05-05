package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.entities.Address;
import com.example.Biluthyrningssystem.entities.Customer;
import com.example.Biluthyrningssystem.exceptions.ResourceNotFoundException;
import com.example.Biluthyrningssystem.repositories.AddressRepository;
import com.example.Biluthyrningssystem.repositories.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
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

        Optional<Customer> schrodingersCustomer = customerRepository.findById(personnummer);
        if(schrodingersCustomer.isPresent()){
            return schrodingersCustomer.get();
        }
        throw new ResourceNotFoundException("Customer", "personnummer", personnummer);

    }

    @Override
    public Customer addCustomer(Customer newCustomer) {
        if(newCustomer.getFirstName() == null || newCustomer.getFirstName().isEmpty()) {
            // Utför åtgärd
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer must have a first name");
        }

        if(newCustomer.getLastName() == null || newCustomer.getLastName().isEmpty()) {
            // Utför åtgärd
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer must have a last name");
        }

        if(newCustomer.getPersonnummer() == null || newCustomer.getPersonnummer().isEmpty()) {
            // Utför åtgärd
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer must have a personnummer");
        }

        if(newCustomer.getAddress().getId() == null) {
            // Utför åtgärd
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer must have an address");
        }

        if(newCustomer.getEmail() == null || newCustomer.getEmail().isEmpty()) {
            // Utför åtgärd
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer must have a email address");
        }

        if(newCustomer.getPhone() == null || newCustomer.getPhone().isEmpty()) {
            // Utför åtgärd
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer must have a phone number");
        }


        Long addressId = newCustomer.getAddress().getId();

        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        newCustomer.setAddress(existingAddress);

        Customer savedCustomer = customerRepository.save(newCustomer);
        logger.info("New customer added to database with Personnummer: {}", savedCustomer.getPersonnummer());
        return savedCustomer;
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

    public void deleteCustomer(String personnummer) {
        Optional<Customer> schrodingersCustomer = customerRepository.findById(personnummer);
        if(schrodingersCustomer.isPresent()){
            customerRepository.delete(schrodingersCustomer.get());
            return;
        }else
        // Implementera loggning
        throw new ResourceNotFoundException("Customer", "personnumer", personnummer);
    }
}
