package com.example.Biluthyrningssystem.controllers;

import com.example.Biluthyrningssystem.entities.Customer;
import com.example.Biluthyrningssystem.services.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
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

    @GetMapping("/admin/customer/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/updateinfo")
    public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customerToUpdate) {
        return ResponseEntity.ok(customerService.updateCustomer(customerToUpdate));
    }

    @PostMapping("/admin/addcustomer")
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customerToAdd) {
        return ResponseEntity.ok(customerService.addCustomer(customerToAdd));
    }

    @DeleteMapping("/admin/removecustomer/{id}")
    public ResponseEntity<String> removeCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Member deleted");
    }

}
