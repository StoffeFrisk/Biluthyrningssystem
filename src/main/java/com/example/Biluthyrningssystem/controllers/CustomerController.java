package com.example.Biluthyrningssystem.controllers;

import com.example.Biluthyrningssystem.services.CustomerService;
import com.example.Biluthyrningssystem.services.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CustomerController {

    private final CustomerServiceImpl customerService;

    @Autowired
    public CustomerController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }
}
