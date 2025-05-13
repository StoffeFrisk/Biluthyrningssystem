package com.example.Biluthyrningssystem.dto;

public class CustomerRevenueDTO {

    private String customerId;
    private String customerName;
    private Double customerRevenue;

    public CustomerRevenueDTO(String customerId, String customerName, Double customerRevenue) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerRevenue = customerRevenue;
    }
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Double getCustomerRevenue() {
        return customerRevenue;
    }

    public void setCustomerRevenue(Double customerRevenue) {
        this.customerRevenue = customerRevenue;
    }
}
