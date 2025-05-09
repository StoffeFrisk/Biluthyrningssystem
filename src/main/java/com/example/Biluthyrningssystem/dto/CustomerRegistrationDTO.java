package com.example.Biluthyrningssystem.dto;

import com.example.Biluthyrningssystem.entities.Customer;

public class CustomerRegistrationDTO {  //Lynsey Fox

    private Customer customer;
    private String loginDetails;

    public CustomerRegistrationDTO(Customer customer, String loginDetails) {
        this.customer = customer;
        this.loginDetails = loginDetails;

    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getLoginDetails() {
        return loginDetails;
    }

    public void setLoginDetails(String loginDetails) {
        this.loginDetails = loginDetails;
    }

    @Override
    public String toString() {
        return "CustomerRegistrationDTO{" +
                "customer=" + customer +
                ", loginDetails='" + loginDetails + '\'' +
                '}';
    }
}
