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
    String username;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, AddressRepository addressRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
    }


    @Override
    public List<Customer> getAllCustomers() {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        username = authentication.getName();
        logger.info("Customer list accessed by user: {}", username);

        return customerRepository.findAll();

    }

    @Override
    public Customer getCustomerByPersonnummer(String personnummer) {

        Optional<Customer> schrodingersCustomer = customerRepository.findById(personnummer);
        if (schrodingersCustomer.isPresent()) {
            Customer customerToShow = schrodingersCustomer.get();

            authentication = SecurityContextHolder.getContext().getAuthentication();
            username = authentication.getName();
            logger.info("Customer with Personnummer: {} details accessed by user: {}", customerToShow.getPersonnummer(), username);

            return schrodingersCustomer.get();
        }
        logger.warn("Customer with Personnummer: {} not found, search performed by user: {}", personnummer, username);
        throw new ResourceNotFoundException("Customer", "personnummer", personnummer);

    }

    @Override
    public Customer addCustomer(Customer newCustomer) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        username = authentication.getName();

        validateStringField(newCustomer.getFirstName(), "first name");
        validateStringField(newCustomer.getLastName(), "last name");
        validateStringField(newCustomer.getPersonnummer(), "personnummer");
        validateStringField(newCustomer.getEmail(), "email address");
        validateStringField(newCustomer.getPhone(), "phone number");

        if (!isValidEmail(newCustomer.getEmail())) {
            logger.warn("Invalid email format: {}, performed by user: {}", newCustomer.getEmail(), username);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }

        if (!isValidPhone(newCustomer.getPhone())) {
            logger.warn("Invalid phone number format: {}, performed by user: {}", newCustomer.getPhone(), username);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone number format");
        }

        if (newCustomer.getAddress() == null || newCustomer.getAddress().getId() == null) {
            logger.warn("Missing or invalid address for customer, performed by user: {}", username);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer must have an address with a valid ID");
        }

        Long addressId = newCustomer.getAddress().getId();

        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    logger.warn("Address not found with ID: {}, performed by user: {}", addressId, username);
                    return new ResourceNotFoundException("Address", "id", addressId);
                });

        newCustomer.setAddress(existingAddress);

        Customer savedCustomer = customerRepository.save(newCustomer);

        logger.info("New customer added to database with Personnummer: {} performed by user: {}", savedCustomer.getPersonnummer(), username);
        return savedCustomer;
    }

    private void validateStringField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            logger.warn("Missing field: {}, performed by user: {}", fieldName, username);
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
        username = authentication.getName();

        if (existing.getPersonnummer() != authentication.getPrincipal()) {
            logger.warn("User: {} attempted to update another user's details", username);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only update your own details");
        }

        existing.setFirstName(customerToUpdate.getFirstName());
        existing.setLastName(customerToUpdate.getLastName());
        existing.setEmail(customerToUpdate.getEmail());
        existing.setPhone(customerToUpdate.getPhone());

        Long addressId = customerToUpdate.getAddress().getId();
        Address existingAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    logger.warn("Address not found with ID: {}, performed by user: {}", addressId, username);
                    return new ResourceNotFoundException("Address", "id", addressId);
                });

        existing.setAddress(existingAddress);

        logger.info("Customer details updated in database for customer with Personnummer: {} by user: {}", customerToUpdate.getPersonnummer(), username);

        return customerRepository.save(existing);
    }

    @Override

    public void deleteCustomer(String personnummer) {
        Optional<Customer> schrodingersCustomer = customerRepository.findById(personnummer);
        if (schrodingersCustomer.isPresent()) {
            customerRepository.delete(schrodingersCustomer.get());
            Customer customerToDelete = schrodingersCustomer.get();

            authentication = SecurityContextHolder.getContext().getAuthentication();
            username = authentication.getName();

            logger.info("Customer with personnummer: {} removed from database by user: {}", customerToDelete.getPersonnummer(), username);

        } else {
            logger.warn("Customer not found with personnummer: {}, performed by user: {}", personnummer, username);
            throw new ResourceNotFoundException("Customer", "personnumer", personnummer);
        }

    }
}
