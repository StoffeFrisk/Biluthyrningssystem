package com.example.Biluthyrningssystem.controllers;

import com.example.Biluthyrningssystem.dto.CustomerRegistrationDTO;
import com.example.Biluthyrningssystem.entities.Customer;
import com.example.Biluthyrningssystem.services.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {       //Lynsey Fox

    private final CustomerServiceImpl customerService;

    @Autowired
    public CustomerController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/admin/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/admin/customer/{personnummer}")

    public ResponseEntity<Customer> getCustomerById(@PathVariable String personnummer) {
        return ResponseEntity.ok(customerService.getCustomerByPersonnummer(personnummer));

    }

    @PutMapping("/updateinfo")
    public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customerToUpdate) {
        return ResponseEntity.ok(customerService.updateCustomer(customerToUpdate));
    }

    @PostMapping("/admin/addcustomer")
    public ResponseEntity<CustomerRegistrationDTO> addCustomer(@RequestBody Customer customerToAdd) {
        return ResponseEntity.ok(customerService.addCustomer(customerToAdd));
    }

    @DeleteMapping("/admin/removecustomer/{personnummer}")
    public ResponseEntity<String> removeCustomer(@PathVariable String personnummer) {
        customerService.deleteCustomer(personnummer);
        return ResponseEntity.ok(" Customer with personnummer " + personnummer + " successfully deleted");
    }

}
