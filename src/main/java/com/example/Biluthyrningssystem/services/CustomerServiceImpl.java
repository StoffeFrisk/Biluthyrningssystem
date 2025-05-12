package com.example.Biluthyrningssystem.services;

import com.example.Biluthyrningssystem.dto.CustomerRegistrationDTO;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class CustomerServiceImpl implements CustomerService { //Lynsey Fox

    private final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);
    Authentication authentication;
    private final InMemoryUserDetailsManager userDetailsManager;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, AddressRepository addressRepository, InMemoryUserDetailsManager userDetailsManager) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.userDetailsManager = userDetailsManager;
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
    public CustomerRegistrationDTO addCustomer(Customer newCustomer) {

        String personnummerToAdd = newCustomer.getPersonnummer();

        if (customerRepository.existsByPersonnummer(personnummerToAdd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer already exists with given personnummer");
        }

        if (userDetailsManager.userExists(personnummerToAdd)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists with given personnummer");
        }

        if(!isValidPersonnummer(newCustomer.getPersonnummer())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid personnummer: format should be xxxxxxxx-xxxx");
        }

        validateDetails(newCustomer);

        Long addressId = newCustomer.getAddress().getId();

        Address existingAddress = findAddressById(addressId);

        newCustomer.setAddress(existingAddress);

        Customer savedCustomer = customerRepository.save(newCustomer);
        String username = savedCustomer.getPersonnummer();
        String password = username.substring(username.length() - 4);


        UserDetails user = User.withUsername(personnummerToAdd)
                .password("{noop}" + password)
                .roles("USER")
                .build();

        userDetailsManager.createUser(user);
        logger.info("Created user login for personnummer: {}", personnummerToAdd);

        logger.info("New customer added to database with Personnummer: {}", savedCustomer.getPersonnummer());

        String loginInfo = "Login username: " + username + ", password: " + password;

        return new CustomerRegistrationDTO(savedCustomer,loginInfo);
    }

    private void validateStringField(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            logger.error("Missing field: {}", fieldName);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer must have a " + fieldName);
        }
    }

    private boolean isValidPersonnummer(String personnummer) {
        String personnummerRegex = "^[0-9]{8}-[0-9]{4}";
        return Pattern.matches(personnummerRegex, personnummer);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return Pattern.matches(emailRegex, email);
    }

    private boolean isValidPhone(String phone) {
        String phoneRegex = "^(\\d{10}|\\+\\d{12})$";
        return Pattern.matches(phoneRegex, phone);
    }

    private void validateDetails(Customer customerToCheck){
        validateStringField(customerToCheck.getFirstName(), "first name");
        validateStringField(customerToCheck.getLastName(), "last name");
        validateStringField(customerToCheck.getPersonnummer(), "personnummer");
        validateStringField(customerToCheck.getEmail(), "email address");
        validateStringField(customerToCheck.getPhone(), "phone number");

        if (!isValidEmail(customerToCheck.getEmail())) {
            logger.error("Invalid email format: {}", customerToCheck.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }

        if (!isValidPhone(customerToCheck.getPhone())) {
            logger.error("Invalid phone number format: {}", customerToCheck.getPhone());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid phone number format");
        }

        if (customerToCheck.getAddress() == null || customerToCheck.getAddress().getId() == null) {
            logger.error("Missing or invalid address for customer");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer must have an address with a valid ID");
        }

    }

    private Address findAddressById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> {
                    logger.error("Address not found with ID: {}", addressId);
                    return new ResourceNotFoundException("Address", "id", addressId);
                });
    }
    @Override
    public Customer updateCustomer(Customer customerToUpdate) {
        Customer existing = getCustomerByPersonnummer(customerToUpdate.getPersonnummer());

        authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!existing.getPersonnummer().equals(authentication.getName())) {
            logger.error("Attempted to update another user's details");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user can only update their own details");
        }

        validateDetails(customerToUpdate);

        existing.setFirstName(customerToUpdate.getFirstName());
        existing.setLastName(customerToUpdate.getLastName());
        existing.setEmail(customerToUpdate.getEmail());
        existing.setPhone(customerToUpdate.getPhone());

        Long addressId = customerToUpdate.getAddress().getId();
        Address existingAddress = findAddressById(addressId);

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
            throw new ResourceNotFoundException("Customer", "personnummer", personnummer);
        }

    }
}
