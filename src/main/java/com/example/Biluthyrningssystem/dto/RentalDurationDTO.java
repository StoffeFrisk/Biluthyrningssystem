package com.example.Biluthyrningssystem.dto;

public class RentalDurationDTO {

    private int days;
    private long orders;

    public RentalDurationDTO(int days, long orders) {
        this.days = days;
        this.orders = orders;
    }

    public int getDays() {
        return days;
    }

    public long getOrders() {
        return orders;
    }
}
