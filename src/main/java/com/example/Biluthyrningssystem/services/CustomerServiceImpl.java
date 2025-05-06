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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    Authentication authentication;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, AddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }


    @Override
    public List<Customer> getAllCustomers() {
        logger.info("Customer list accessed");
        return customerRepository.findAll();

    }

    @Override
    public Customer getCustomerByPersonnummer(String personnummer) {

        Optional<Customer> schrodingersCustomer = customerRepository.findById(personnummer);
        if (schrodingersCustomer.isPresent()) {
            Customer customerToShow = schrodingersCustomer.get();

            logger.info("Customer with Personnummer: {} details accessed", customerToShow.getPersonnummer());

            return schrodingersCustomer.get();
        }
        logger.error("Customer with Personnummer: {} not found", personnummer);
        throw new ResourceNotFoundException("Customer", "personnummer", personnummer);

    }

    @Override
    public Customer addCustomer(Customer newCustomer) {

        validateStringField(newCustomer.getFirstName(), "first name");
        validateStringField(newCustomer.getLastName(), "last name");
        validateStringField(newCustomer.getPersonnummer(), "personnummer");
        validateStringField(newCustomer.getEmail(), "email address");
        validateStringField(newCustomer.getPhone(), "phone number");

        if (!isValidEmail(newCustomer.getEmail())) {
            logger.error("Invalid email format: {}", newCustomer.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }

        if (!isValidPhone(newCustomer.getPhone())) {
            logger.error("Invalid phone number format: {}", newCustomer.getPhone());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone number format");
        }

        if (newCustomer.getAddress() == null || newCustomer.getAddress().getId() == null) {
            logger.error("Missing or invalid address for customer");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer must have an address with a valid ID");
        }

        Long addressId = newCustomer.getAddress().getId();

        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    logger.error("Address not found with ID: {}", addressId);
                    return new ResourceNotFoundException("Address", "id", addressId);
                });

        newCustomer.setAddress(existingAddress);

        Customer savedCustomer = customerRepository.save(newCustomer);

        logger.info("New customer added to database with Personnummer: {}", savedCustomer.getPersonnummer());
        return savedCustomer;
    }

    private void validateStringField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            logger.error("Missing field: {}", fieldName);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer must have a " + fieldName);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidPhone(String phone) {
        String phoneRegex = "^(\\d{10}|\\+\\d{12})$";
        return Pattern.matches(phoneRegex, phone);
    }

    @Override
    public Customer updateCustomer(Customer customerToUpdate) {
        Customer existing = getCustomerByPersonnummer(customerToUpdate.getPersonnummer());

        authentication = SecurityContextHolder.getContext().getAuthentication();

        if (existing.getPersonnummer() != authentication.getPrincipal()) {
            logger.error("You attempted to update another user's details");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only update your own details");
        }

        existing.setFirstName(customerToUpdate.getFirstName());
        existing.setLastName(customerToUpdate.getLastName());
        existing.setEmail(customerToUpdate.getEmail());
        existing.setPhone(customerToUpdate.getPhone());

        Long addressId = customerToUpdate.getAddress().getId();
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    logger.error("Address not found with ID: {}", addressId);
                    return new ResourceNotFoundException("Address", "id", addressId);
                });

        existing.setAddress(existingAddress);

        logger.info("Customer details updated in database for customer with Personnummer: {}", customerToUpdate.getPersonnummer());

        return customerRepository.save(existing);
    }

    @Override

    public void deleteCustomer(String personnummer) {
        Optional<Customer> schrodingersCustomer = customerRepository.findById(personnummer);
        if (schrodingersCustomer.isPresent()) {
            customerRepository.delete(schrodingersCustomer.get());
            Customer customerToDelete = schrodingersCustomer.get();

            logger.info("Customer with personnummer: {} removed from database.", customerToDelete.getPersonnummer());

        } else {
            logger.error("Customer not found with personnummer: {}", personnummer);
            throw new ResourceNotFoundException("Customer", "personnumer", personnummer);
        }

    }
}
